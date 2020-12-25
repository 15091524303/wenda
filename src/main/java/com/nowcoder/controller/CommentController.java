package com.nowcoder.controller;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SensitiveService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {   //对提问的评论
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;   //在threadLocal中的当前线程的用户信息

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    SensitiveService sensitiveService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,       //添加评论
                             @RequestParam("content") String content) {
        try {
            // 过滤content
            content = HtmlUtils.htmlEscape(content);     //对内容先过滤一遍html的干扰
            content = sensitiveService.filter(content);   //过滤敏感词
            Comment comment = new Comment();   //创建一个评论对象
            if (hostHolder.getUser() != null) {    //若当前有用户登录，评论的userId即为当前的User的Id
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);  //若当前用户没有登录，则评论的userId即为系统匿名用户的Id
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);    //对问题的评论
            comment.setCreatedDate(new Date());
            comment.setStatus(0);   //0表示正常评论

            commentService.addComment(comment);   //敏感词过滤过滤了两遍----------------------------------------------------------
            // 更新题目里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
