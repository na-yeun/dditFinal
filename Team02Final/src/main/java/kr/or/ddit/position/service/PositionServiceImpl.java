package kr.or.ddit.position.service;

import kr.or.ddit.position.dao.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    PositionMapper mapper;

    @Override
    public String readPositionByPosiId(String posiId) {
        return mapper.selectPosiNameByPosiId(posiId);
    }
}
