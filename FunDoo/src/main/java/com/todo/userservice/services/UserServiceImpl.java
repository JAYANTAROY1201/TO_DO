package com.todo.userservice.services;

import java.util.Optional;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.todo.noteservice.services.NoteServiceImpl;
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
	private IUserRepository userRepository;
	@Autowired
	RabbitMQSender rabbitSender;
	@Autowired
	PasswordEncoder passwordencoder;
	@Autowired
	IMailService mailService;
	@Autowired
	IRedisRepository<String, User> redisImpl;
	@Autowired
	Messages messages;
	@Value("${hostandport}")
	String host;
	
	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	/**
	 * This method is add functionality for sign up
	 * 
	 * @param user
	 * @return true if sign up successful else false
	 * @throws SignupException
	 */
	@Override
	public void doSignUp(User user) throws SignupException {
		logger.debug(messages.get("217"));
		Preconditions.checkNotNull(user.getEmail(),messages.get("209"));
		Preconditions.checkNotNull(user.getMobile(),messages.get("210"));
		Preconditions.checkNotNull(user.getPassword(),messages.get("211"));
		Preconditions.checkNotNull(user.getUserName(),messages.get("212"));
		
			Optional<User> userOp = userRepository.findByEmail(user.getEmail());
			if (userOp.isPresent()) {
				throw new SignupException(messages.get("213"));
			} else {
                user.set_id(getNextSequence("sequence"));
				user.setPassword(passwordencoder.encode(user.getPassword()));
				user.setActivation("false");
				userRepository.save(user);
			}
			logger.debug(messages.get("218"));
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
		logger.debug(messages.get("219"));
		Preconditions.checkNotNull(loginCredentials.getEmail(),messages.get("209"));
		Preconditions.checkNotNull(loginCredentials.getPassword(),messages.get("211"));		
		if (userRepository.findByEmail(loginCredentials.getEmail()).isPresent() == false) {
			throw new LoginException(messages.get("214"));
		}
		if (userRepository.findByEmail(loginCredentials.getEmail()).get().getActivation().equals("false")) {
			throw new LoginException(messages.get("215"));
		}
		if (!passwordencoder.matches(loginCredentials.getPassword(), userRepository.findByEmail(loginCredentials.getEmail()).get().getPassword())) {
			throw new LoginException(messages.get("216"));
		} 
		else 
		{
			User user = new User();
			user = userRepository.findByEmail(loginCredentials.getEmail()).get();
			JwtTokenBuilder jwt = new JwtTokenBuilder();
			redisImpl.setToken(jwt.createJWT(user));
			logger.debug(messages.get("220"));
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
		logger.debug(messages.get("221"));
		String body = "Click here to activate your account:\n\n" + host + "/fundoo/user/activateaccount/?" + jwt;
		rabbitSender.send(to, "Email Activation Link", body);
		logger.debug(messages.get("222"));
	}

	/**
	 * This method is written to activate the account
	 * 
	 * @param jwt
	 * @throws AccountActivationException
	 */
	public void doActivateEmail(String jwt) throws AccountActivationException {
		logger.debug(messages.get("223"));
		Claims claims = JwtTokenBuilder.parseJWT(jwt);
		if (userRepository.findById(claims.getId()).isPresent() == false) {
			throw new AccountActivationException(messages.get("215"));
		} else {
			Optional<User> user = userRepository.findById(claims.getId());
			user.get().setActivation("true");
			userRepository.save(user.get());
		}
		logger.debug(messages.get("224"));
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
		logger.debug(messages.get("225"));
		JwtTokenBuilder jb = new JwtTokenBuilder();
		if (userRepository.findByEmail(email).isPresent() == false) {
			throw new LoginException(messages.get("214"));
		}
		String body = "Copy the below link to postman and reset your password:\n\n" + host
				+ "/fundoo/user/resetpassword/?" + jb.createJWT(userRepository.findByEmail(email).get());
		mailService.sendMail(email, "Password reset mail", body);
		logger.debug(messages.get("226"));
	}

	/**
	 * Method to reset password
	 * 
	 * @param jwtToken
	 * @param newPassword
	 */
	@Override
	public void doResetPassword(String jwtToken, String newPassword) {
		logger.debug(messages.get("227"));
		Claims claims = JwtTokenBuilder.parseJWT(jwtToken);
		Optional<User> user = userRepository.findById(claims.getId());
		user.get().setPassword(passwordencoder.encode(newPassword));
		userRepository.save(user.get());
		logger.debug(messages.get("228"));
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
