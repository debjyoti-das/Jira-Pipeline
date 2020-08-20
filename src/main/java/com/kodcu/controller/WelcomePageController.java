package com.kodcu.controller;

/*
 * Created by hakdogan on 23/11/2017
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WelcomePageController {

    @RequestMapping(value = "/debjyoti/app", method = RequestMethod.GET)
    public String getPage(){
        return "index";
    }
}
