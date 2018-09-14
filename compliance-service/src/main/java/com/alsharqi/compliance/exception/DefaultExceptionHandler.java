package com.alsharqi.compliance.exception;


import com.alsharqi.compliance.response.DefaultResponse;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<DefaultResponse> processValidationError(MethodArgumentNotValidException ex) {
       return new ResponseEntity<DefaultResponse>(new DefaultResponse("F001","Field Should nnot be NULL","null"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<DefaultResponse> processRuntimeException(ObjectNotFoundException exception) { //--- the "throw exception object is passed as a paremeter "
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F100");
        response.setDescription("Relative resouece against Id is not found");
        response.setResponseIdentifier(exception.getMessage());


        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK);
    }

    @ExceptionHandler( NumberFormatException.class)
    public ResponseEntity<DefaultResponse> StringInsteadOfLong(NumberFormatException e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F001");
        response.setDescription(e.getMessage());
        response.setResponseIdentifier("ID format is wrong"); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }



    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<DefaultResponse> idMustNotBeNull(InvalidDataAccessApiUsageException e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F102");
        response.setDescription(e.getMessage());
        response.setResponseIdentifier("null"); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }



    @ExceptionHandler( IllegalArgumentException.class)
    public ResponseEntity<DefaultResponse> NullIdPasssed(InvalidDataAccessApiUsageException e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F102");
        response.setDescription(e.getMessage());
        response.setResponseIdentifier("null"); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }

    @ExceptionHandler( Exception.class)
    public ResponseEntity<DefaultResponse> GeneralException(Exception e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F001");
        response.setDescription(e.getMessage());
        response.setResponseIdentifier("null"); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }

    @ExceptionHandler( EmptyEntityTableException.class)
    public ResponseEntity<DefaultResponse> entityTableEmpty(EmptyEntityTableException e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F001");
        response.setDescription(e.getDescription());
        response.setResponseIdentifier(e.getResourseID().toString()); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }


    @ExceptionHandler( ForeignKeyContraintException.class)
    public ResponseEntity<DefaultResponse> foreignKeyContraintErrorDeleteUpdate(ForeignKeyContraintException e){
        DefaultResponse response = new DefaultResponse();
        response.setResponseCode("F001");
        response.setDescription(e.getDescription());
        response.setResponseIdentifier(e.getResourseID().toString()); //---  given id null, response identifier null

        return new ResponseEntity<DefaultResponse>(response, HttpStatus.OK); //---- as we do not requier field
    }

}
