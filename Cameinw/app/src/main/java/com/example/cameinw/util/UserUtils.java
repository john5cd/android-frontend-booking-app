package com.example.cameinw.util;

import com.example.cameinw.dto.UserDTO;
import com.example.cameinw.model.User;

public class UserUtils {
    public static User processUserDTO(UserDTO userDTO) {
        User user = convertToUser(userDTO);
        return user;
    }

    public  static User convertToUser(UserDTO userDTO) {
        User user = new User();

        user.setId(userDTO.getUserId());
        user.setTheUserName(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhone());

        return user;
    }
}
