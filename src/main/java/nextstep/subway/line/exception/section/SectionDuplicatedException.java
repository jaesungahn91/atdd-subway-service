package nextstep.subway.line.exception.section;

import nextstep.subway.common.exception.ServiceException;
import org.springframework.http.HttpStatus;

/**
 * packageName : nextstep.subway.common.exception
 * fileName : SectionDuplicatedException
 * author : haedoang
 * date : 2021/12/01
 * description : 중복 구간 예외 클래스
 */
public class SectionDuplicatedException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public static final HttpStatus status = HttpStatus.BAD_REQUEST;
    public static final String message = "이미 등록된 구간 입니다.";

    public SectionDuplicatedException() {
        super(status, message);
    }
}