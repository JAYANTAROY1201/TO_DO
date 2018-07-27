package com.todo.userservice.services;

import java.util.Optional;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todo.exception.AccountActivationException;
import com.todo.exception.LoginException;
import com.todo.exception.SignupException;
import com.todo.userservice.dao.GeneralMongoRepository;
import com.todo.userservice.dao.MailService;
import com.todo.userservice.model.Sequence;
import com.todo.userservice.model.User;
import com.todo.utility.JwtTokenBuilder;
import com.todo.utility.RabbitMQSender;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.data.mongodb.core.MongoOperations;
import io.jsonwebtoken.Claims;

/**
 * purpose: Implementation of user service
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@Service
public class UserServiceImpl {
	@Autowired 
	private MongoOperations mongo;
	@Autowired
	private GeneralMongoRepository gm;
	@Autowired
	RabbitMQSender rabbitSender;
	@Autowired
	PasswordEncoder passwordencoder;
	@Autowired
	MailService mailService;
	
	
	@Value("${hostandport}")
	String host;

	/**
	 * This method is add functionality for sign up
	 * 
	 * @param user
	 * @return true if sign up successful else false
	 * @throws SignupException
	 */
	public void doSignUp(User user) throws SignupException {
		if (user.getEmail().equals("")) {
			throw new SignupException("Email is null");
		} else {
			Optional<User> userOp = gm.findByEmail(user.getEmail());
			if (userOp.isPresent()) {
				throw new SignupException("Email already exist");
			} else {
                user.set_id(getNextSequence("sequence"));
				user.setPassword(passwordencoder.encode(user.getPassword()));
				user.setActivation("false");
				gm.save(user);
			}
		}
	}

	/**
	 * This method is add functionality for login
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws LoginException
	 */
	public String doLogIn(String email, String password) throws LoginException {
		if (email.equals("")) {
			throw new LoginException("Email can't be null");
		}
		if (password.equals("")) {
			throw new LoginException("Password cannot be blank");
		}
		if (gm.findByEmail(email).isPresent() == false) {
			throw new LoginException("Email not found");
		}
		if (gm.findByEmail(email).get().getActivation().equals("false")) {
			throw new LoginException("Account not activated");
		}
		if (!passwordencoder.matches(password, gm.findByEmail(email).get().getPassword())) {
			throw new LoginException("Password not correct");
		} else {
			User user = new User();
			user = gm.findByEmail(email).get();
			user.toString();
			JwtTokenBuilder jwt = new JwtTokenBuilder();
			return jwt.createJWT(user);
		}

	}

	/**
	 * This method is written to task send an activation link to registered email
	 * 
	 * @param jwToken
	 * @param emp
	 * @throws MessagingException
	 */
	public void sendActivationLink(String to, String jwt) throws MessagingException {
		String body = "Click here to activate your account:\n\n" + host + "/fundoo/user/activateaccount/?" + jwt;
		rabbitSender.send(to, "Email Activation Link", body);
	}

	/**
	 * This method is written to activate the account
	 * 
	 * @param jwt
	 * @throws AccountActivationException
	 */
	public void doActivateEmail(String jwt) throws AccountActivationException {
		Claims claims = JwtTokenBuilder.parseJWT(jwt);
		if (gm.findById(claims.getId()).isPresent() == false) {
			throw new AccountActivationException("Account not get activated");
		} else {
			Optional<User> user = gm.findById(claims.getId());
			user.get().setActivation("true");
			gm.save(user.get());
		}
	}

	/**
	 * This method is written to send password to the registered email to if user
	 * forget password
	 * 
	 * @param email
	 * @return true if mail sent successfully else false
	 * @throws LoginException
	 * @throws MessagingException
	 */
	public void doSendNewPasswordLink(String email) throws LoginException, MessagingException {
		JwtTokenBuilder jb = new JwtTokenBuilder();
		if (gm.findByEmail(email).isPresent() == false) {
			throw new LoginException("Email not exist");
		}
		String body = "Copy the below link to postman and reset your password:\n\n" + host
				+ "/fundoo/user/resetpassword/?" + jb.createJWT(gm.findByEmail(email).get());
		mailService.sendMail(email, "Password reset mail", body);
	}

	/**
	 * Method to reset password
	 * 
	 * @param jwtToken
	 * @param newPassword
	 */
	public void doResetPassword(String jwtToken, String newPassword) {
		Claims claims = JwtTokenBuilder.parseJWT(jwtToken);
		Optional<User> user = gm.findById(claims.getId());
		user.get().setPassword(passwordencoder.encode(newPassword));
		gm.save(user.get());
	}
	
	
	public String getNextSequence(String seqName)
    {
        Sequence counter = mongo.findAndModify(
            query(where("_id").is(seqName)),
            new Update().inc("seq",1),
            options().returnNew(true).upsert(true),
            Sequence.class);
        return counter.getSeq()+"";
    }
}
