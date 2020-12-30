package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CommentController {
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

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,       //添加对问题的评论
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
            // 更新题目里的评论数量   对问题的评论个数应该包括对问题的评论数加上评论的评论数，这里未做更新
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());//对所有的评论
            questionService.updateCommentCount(comment.getEntityId(), count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }

    @RequestMapping(path = {"/addCommentComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("content") String content,//添加对评论的评论
                             @RequestParam("commentId") int commentId) {
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
            comment.setEntityId(commentId);
            comment.setEntityType(EntityType.ENTITY_COMMENT);    //对评论的评论
            comment.setCreatedDate(new Date());
            comment.setStatus(0);   //0表示正常评论

            commentService.addComment(comment);
            // 更新评论里的评论数量
//            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());//对评论的评论
            int count = commentService.getCommentCount(comment.getEntityId(), EntityType.ENTITY_COMMENT);//对评论的评论
//            questionService.updateCommentCount(comment.getEntityId(), count);
            commentService.updateCommentCount(commentId, count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/comment/" + commentId;
    }

    @RequestMapping(value = "/comment/{cid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("cid") int cid) {
        Comment comment = commentService.getCommentById(cid);
        model.addAttribute("comment", comment);
//        model.addAttribute("user", userService.getUser(question.getUserId()));
        List<Comment> commentList = commentService.getCommentsByEntity(cid, EntityType.ENTITY_COMMENT);//获得对评论的评论列表
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comm : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comm);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }

        model.addAttribute("commentsComments", comments);  //对评论的评论

        //对评论的评论无需关注功能

        return "commentDetail";
    }
}
