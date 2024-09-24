package com.pard.root.content.controller;

import com.pard.root.content.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/content")
public class ContentController {
    private ContentService contentService;

}
