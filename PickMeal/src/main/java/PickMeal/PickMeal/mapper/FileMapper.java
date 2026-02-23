package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    List<File> findByBoardId(long boardId);

    void save(File fileEntity);

    void deleteByBoardId(long bno);

    File findById(long fileId);
}