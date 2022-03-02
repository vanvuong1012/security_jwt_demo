package com.example.security_demo.service;

import com.example.security_demo.dto.User;
import com.example.security_demo.repository.UserRepository;
import com.example.security_demo.request.EditAccountRequest;
import com.example.security_demo.response.ApiException;
import com.example.security_demo.response.ERROR;
import com.example.security_demo.response.MainResponse;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findOne(String username){
        return userRepository.findByUsername(username);
    }

    public MainResponse<String> editAccount(EditAccountRequest request) throws ApiException{
        User existedAccount = this.validateRequestEditedAccount(request);
        String passwordEncode = passwordEncoder.encode(request.getPassword());
        existedAccount.setPassword(passwordEncode);
        userRepository.save(existedAccount);
        return new MainResponse<>();
    }

    private User validateRequestEditedAccount(EditAccountRequest request) throws ApiException{
        if(StringUtils.isBlank(request.getUserId().toString())){
            throw new ApiException(ERROR.INVALID_PARAM, "User ID không được để trống");
        }

        if(StringUtils.isBlank(request.getPassword())){
            throw new ApiException(ERROR.INVALID_PARAM,"Mật khẩu không được để trống");
        }

        if(request.getPassword().length()<0){
            throw new ApiException(ERROR.INVALID_PARAM,"")
        }

    }

    public void register(User user)  {
           String encodePassword = passwordEncoder.encode((user.getPassword()));
            user.setPassword(encodePassword);
            user.getPassword();
            user.getUsername();
            user.getEmail();

            user.setEnabled(false);
            String randomCode = RandomString.make(64);
            user.setVerification_code(randomCode);
            userRepository.save(user);
    }
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerification_code(null);
            user.setEnabled(true);
            userRepository.enabled(user.getId());

            return true;
        }
    }

    }