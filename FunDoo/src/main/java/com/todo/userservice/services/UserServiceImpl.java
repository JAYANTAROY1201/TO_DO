package com.todo.userservice.services;

import java.util.Optional;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.todo.exception.AccountActivationException;
import com.todo.exception.LoginException;
import com.todo.exception.SignupException;
import com.todo.noteservice.dao.IRedisRepository;
import com.todo.userservice.dao.IUserRepository;
import com.todo.userservice.dao.IMailService;
import com.todo.userservice.model.LoginDTO;
import com.todo.userservice.model.Sequence;
import com.todo.userservice.model.User;
import com.todo.utility.JwtTokenBuilder;
import com.todo.utility.Messages;
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
public class UserServiceImpl implements IGeneralUserService {
	@Autowired 
	private MongoOperations mongo;
	@Autowired
	private IUserRepository gm;
	@Autowired
	RabbitMQSender rabbitSender;
	@Autowired
	PasswordEncoder passwordencoder;
	@Autowired
	IMailService mailService;
	@Autowired
	IRedisRepository<String, User> redisImpl;
	
	
	
	@Value("${hostandport}")
	String host;

	/**
	 * This method is add functionality for sign up
	 * 
	 * @param user
	 * @return true if sign up successful else false
	 * @throws SignupException
	 */
	@Override
	public void doSignUp(User user) throws SignupException {
		Preconditions.checkNotNull(user.getEmail(),"Email field is blank");
		Preconditions.checkNotNull(user.getMobile(),"Mobile field is blank");
		Preconditions.checkNotNull(user.getPassword(),"Password field is blank");
		Preconditions.checkNotNull(user.getUserName(),"User name field is blank");
		
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
	

	/**
	 * This method is add functionality for login
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws LoginException
	 */
	@Override
	public String doLogIn(LoginDTO loginCredentials) throws LoginException {	
		Preconditions.checkNotNull(loginCredentials.getEmail(),"Email can't be null");
		Preconditions.checkNotNull(loginCredentials.getPassword(),"Password cannot be blank");		
		if (gm.findByEmail(loginCredentials.getEmail()).isPresent() == false) {
			throw new LoginException("Email not found");
		}
		if (gm.findByEmail(loginCredentials.getEmail()).get().getActivation().equals("false")) {
			throw new LoginException("Account not activated");
		}
		if (!passwordencoder.matches(loginCredentials.getPassword(), gm.findByEmail(loginCredentials.getEmail()).get().getPassword())) {
			throw new LoginException("Password not correct");
		} else {
			User user = new User();
			user = gm.findByEmail(loginCredentials.getEmail()).get();
			JwtTokenBuilder jwt = new JwtTokenBuilder();
			redisImpl.setToken(jwt.createJWT(user));
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
	@Override
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
