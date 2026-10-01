package com.hanttamhanttam.common.exception;

import com.hanttamhanttam.auth.exception.InvalidCredentialsException;
import com.hanttamhanttam.auth.exception.InvalidRefreshTokenException;
import com.hanttamhanttam.gauge.exception.GaugeNotFoundException;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import com.hanttamhanttam.pattern.exception.PatternPdfNotFoundException;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.InvalidCurrentPageException;
import com.hanttamhanttam.project.exception.InvalidProjectStatusException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.review.exception.ReviewNotFoundException;
import com.hanttamhanttam.worklog.exception.InvalidWorkLogContentException;
import com.hanttamhanttam.worklog.exception.InvalidWorkLogPageException;
import com.hanttamhanttam.worklog.exception.WorkLogNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException e
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
            InvalidRefreshTokenException e
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(PatternNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatternNotFound(
            PatternNotFoundException e
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(PatternPdfNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatternPdfNotFound(
            PatternPdfNotFoundException e
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException e
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(CompletedProjectModificationException.class)
    public ResponseEntity<ErrorResponse> handleCompletedProjectModification(
            CompletedProjectModificationException e
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidProjectStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProjectStatus(
            InvalidProjectStatusException e
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(GaugeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGaugeNotFound(
            GaugeNotFoundException e
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidCurrentPageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrentPage(
            InvalidCurrentPageException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidWorkLogPageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWorkLogPage(
            InvalidWorkLogPageException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(WorkLogNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkLogNotFound(
            WorkLogNotFoundException e
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidWorkLogContentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWorkLogContent(
            InvalidWorkLogContentException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFound(
            ReviewNotFoundException e
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }
}
