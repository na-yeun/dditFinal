package kr.or.ddit.notice.controller;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.notice.service.NoticeService;
import kr.or.ddit.notice.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/{companyId}/notice")
public class NoticeReadController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public String noticeList(
            @RequestParam(value="page", required=false, defaultValue="1") int currentPage,
            @ModelAttribute("simpleCondition") SimpleCondition simpleCondition,
            Model model
    ) {
        PaginationInfo paging = new PaginationInfo();
        paging.setCurrentPage(currentPage);
        paging.setSimpleCondition(simpleCondition);

        List<NoticeVO> noticeList = noticeService.readNoticeList(paging);

        PaginationRenderer renderer = new DefaultPaginationRenderer();
        String pagingHtml = renderer.renderPagination(paging, "paging");

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("pagingHTML", pagingHtml);

        return "notice/noticeList";
    }

    //공지사항 세부 조회
    @GetMapping("/{noticeNo}")
    public String noticeDetail(
            @PathVariable("noticeNo") String noticeNo,
            Model model){

//        String sessionCompanyId = (String) session.getAttribute("companyId");
//        if(!companyId.equals(sessionCompanyId)) {
//            return "redirect:/login";
//        }
        NoticeVO notice = noticeService.readNoticeDetail(noticeNo);
        model.addAttribute("notice", notice);
        return "notice/noticeDetail";
    }

}
