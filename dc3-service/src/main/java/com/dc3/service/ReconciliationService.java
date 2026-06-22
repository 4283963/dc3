package com.dc3.service;

import com.dc3.domain.dto.ReconciliationQueryDTO;
import com.dc3.domain.vo.DifferenceDetailVO;
import com.dc3.domain.vo.ReconciliationSummaryVO;
import com.dc3.common.model.PageResult;

public interface ReconciliationService {

    ReconciliationSummaryVO getSummary(ReconciliationQueryDTO query);

    PageResult<DifferenceDetailVO> getDifferenceDetails(ReconciliationQueryDTO query);
}
