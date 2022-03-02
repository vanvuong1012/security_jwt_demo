package com.example.security_demo.controller;

import com.example.security_demo.MailConfig;
import com.example.security_demo.response.ApiException;
import com.example.security_demo.response.ERROR;
import com.example.security_demo.response.MainResponse;
import com.example.security_demo.cofig.JwtTokenUtil;
import com.example.security_demo.dto.AuthToken;
import com.example.security_demo.dto.LoginUser;
import com.example.security_demo.dto.User;
import com.example.security_demo.service.UserService;
import com.example.security_demo.service.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/token")
public class AuthenticationController {

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    UserService userService;
    
    @Autowired
    UserServiceImpl service;

    @PostMapping("/login")
    public Object login(@RequestBody @Valid LoginUser loginUser) throws ApiException{
        User existedAccount = this.validateLoginRequest(loginUser);

            final User user = userService.findOne(existedAccount.getUsername());

            final String token = jwtTokenUtil.generateToken(user);

            return new MainResponse<>(new AuthToken(user.getUsername(),token));
        }

    private User validateLoginRequest(LoginUser loginUser) throws ApiException{
        if (StringUtils.isBlank(loginUser.getUsername())){
            LOGGER.debug("login - user login fail : username blank");
            throw new ApiException(ERROR.INVALID_REQUEST , "Tên đăng nhập không được để trống");
        }
        if (StringUtils.isBlank(loginUser.getPassword())){
            LOGGER.debug("login - user login fail : password blank");
            throw new ApiException(ERROR.INVALID_REQUEST , "Mật khẩu không được để trống");
        }
        User optionalAccount = userService.findOne(loginUser.getUsername());
        if(optionalAccount == null){
            LOGGER.debug("login - user login fail : user {} not found" , loginUser.getUsername());
            throw new ApiException(ERROR.INVALID_REQUEST , "Người dùng không tồn tại trên hệ thống");
        }
        if(!encoder.matches(loginUser.getPassword(), optionalAccount.getPassword())){
            LOGGER.debug("login - user login fail : user {} password not matching" , loginUser.getUsername());
            throw new ApiException(ERROR.INVALID_REQUEST , "Tên đăng nhập hoặc mật khẩu không đúng, xin vui lòng thử lại!");
        }
        if(!optionalAccount.isEnabled()){
            LOGGER.debug("login - user login fail : user {} not active" , loginUser.getUsername());
            throw new ApiException(ERROR.INVALID_REQUEST , "Người dùng đã bị vô hiệu hóa trên hệ thống");
        }

        return optionalAccount;
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//        @ExceptionHandler(MethodArgumentNotValidException.class)
//        public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//            Map<String, String> errors = new HashMap<>();
//            ex.getBindingResult().getAllErrors().forEach((error) -> {
//                String fieldName = ((FieldError) error).getField();
//                String errorMessage = error.getDefaultMessage();
//                errors.put(fieldName, errorMessage);
//            });
//            return errors;
//        }
    @PostMapping("/register")
    public String register(@RequestBody User user, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        service.register(user);
        sendVerificationEmail(request, user);
        return "register_success";

        }

        private void sendVerificationEmail(HttpServletRequest request,User user) throws MessagingException, UnsupportedEncodingException {

            String toAddress = user.getEmail();
            String fromAddress = "vuongnguyen1250070@gmail.com";
            String senderName = "Vuong";
            String subject = "Please verify your registration";
            String content = "NguyenVanVuong,<br>"
                    + "Please click the link below to verify your registration:<br>"
                    + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                    + "Thank you,<br>"
                    + "Amit";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);

            content = content.replace("[[name]]", user.getUsername());

            String verifyURL = MailConfig.getSiteURl(request) + "/verify?code=" + user.getVerification_code();

            content = content.replace("[[URL]]", verifyURL);

            helper.setText(content, true);

            mailSender.send(message);
            System.out.println("to Address: " + toAddress);
            System.out.println("Verify URL: " + verifyURL);

        }

        @GetMapping("/verify")
        public String verifyUser(@Param("code") String code){
        if(service.verify(code)){
        return "verify_success";
        }else {
            return "very_fail";
        }
    }


}

