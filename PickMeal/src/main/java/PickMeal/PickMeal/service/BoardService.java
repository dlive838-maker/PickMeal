package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public Page<Board> findBoardAll(Pageable pageable) {
        List<Board> boards = boardMapper.findBoardAll(pageable);
        int total = boardMapper.countBoard();
        return new PageImpl<>(boards, pageable, total);
    }

    public void writeBoard(Board board) {
        boardMapper.writeBoard(board);
    }

    public void removeBoard(long boardId) {
        boardMapper.removeBoard(boardId);
    }

    public List<Long> getBoardIdByUser_id(long userId) {return boardMapper.getBoardIdByUser_id(userId);
    }

    public Board getBoardByBoardId(long boardId) {return boardMapper.getBoardByBoardId(boardId);
    }

    public void editBoard(Board board) {
        boardMapper.editBoard(board);
    }

    public List<String> extractImageUrlFromContent(String content) {
        if(content == null || content.isEmpty()) return new ArrayList<>();

        Document doc = Jsoup.parse(content);
        Elements elements = doc.getElementsByTag("img");

        List<String> imageUrls = new ArrayList<>();
        for (Element element : elements){
            imageUrls.add(element.attr("src"));
        }

        return imageUrls;
    }

    public void updateViewCount(long boardId) {
        boardMapper.updateViewCount(boardId);
    }

    public void removeDislikeCount(long boardId) {
        boardMapper.removeDislikeCount(boardId);
    }

    public void removeLikeCount(long boardId) {
        boardMapper.removeLikeCount(boardId);
    }

    public void addLikeCount(long boardId) {
        boardMapper.addLikeCount(boardId);
    }

    public void addDislikeCount(long boardId) {
        boardMapper.addDislikeCount(boardId);
    }
}
