package com.example.demo;

import com.example.demo.beans.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class UserServiceImpl {

    @Value("classpath:data/users.json")
    Resource usersFile;

    ObjectMapper mapper = new ObjectMapper();

    TypeReference<ArrayList<User>> typeRef
            = new TypeReference<ArrayList<User>>() {};

    public ArrayList<User> loadUsers() throws IOException {
        return mapper.readValue(usersFile.getInputStream(), typeRef);
    }

}
