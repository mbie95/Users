package com.example.usersdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author marcin
 */
@Service
class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final int NECESSERY_FIELDS = 3;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public String uploadCSV(MultipartFile csvFile) {
        logger.info("Got file: " + csvFile.toString());
        BufferedReader content;
        try {
            if (!"text/csv".equals(csvFile.getContentType())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Wrong file type!");
            }
            
            content = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), "UTF-8"));;
            logger.info("Read file: " + csvFile.toString());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Problem with file!");
        }
        String buffer;
        logger.info("Start processing file: " + csvFile.toString());
        try {
            while ((buffer = content.readLine()) != null) {
                logger.info("Read line: " + buffer);
                String [] cells = buffer.split(";");
                if (cells.length < NECESSERY_FIELDS)
                    continue;
                cells[2] = redoDate(cells[2]);
                if (cells[0].isBlank() || cells[1].isBlank() || cells[2].isBlank())
                    continue;

                logger.info("Necessary cells: " + cells[0] + " " + 
                            cells[1] + " " + cells[2]);
                User user = new User(
                        StringUtils.capitalize(cells[0].trim().toLowerCase()), 
                        StringUtils.capitalize(cells[1].trim().toLowerCase()), 
                        LocalDate.parse(cells[2], formatter));
                logger.info("User created: " + user);
                if (cells.length > NECESSERY_FIELDS) {
                    logger.info("Phone number: " + cells[3]);
                    try {
                        if (cells[3].length() == 9) {
                            user.setPhoneNumber(Integer.parseInt(cells[3]));
                            logger.info("Phone number added");
                        }
                        else
                            logger.error("Wrong phone number!");
                        if (repository.existsByPhoneNumber(user.getPhoneNumber())) {
                            logger.info("User with this phone number already exists!");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Wrong phone number!");
                    }
                }
                
                repository.save(user);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during processing file data!");
        }
        
        return "Uploaded";
    }
    
    public Long numberOfUsers() {
        logger.info("Write number of users!");
        return repository.count();
    }
    
    public List<User> findAll(int pageNum, int size) {
        logger.info("Write all users!");
        Pageable page = PageRequest.of(pageNum, size, 
                                        Sort.by("birthDate").ascending());
        return repository.findAll(page).getContent();
    }
    
    public User oldestUser() {
        logger.info("Write oldest user!");
        Pageable page = PageRequest.of(0, 1, 
                                        Sort.by("birthDate").ascending()
                                        .and(Sort.by("phoneNumber").descending()));
        if (repository.count() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        return repository.findAll(page).getContent().get(0);
    }
    
    public Optional<User> findUserByLastName(String lastName) {
        logger.info("Write user with last name: " + lastName);
        return repository.findByLastName(lastName);
    }
    
    public String deleteUser(Integer phoneNumber) {
        logger.info("Deleting user with phone number: " + phoneNumber);
        repository.deleteByPhoneNumber(phoneNumber);
        return "User deleted!";
    }
    
    public String deleteUsers() {
        logger.info("Deleting all users!");
        repository.deleteAll();
        return "Users deleted";
    }
    
    private String redoDate(String date) {
        int shorterMonth = 0;
        String newDate = "";
        date = date.trim();
        if (date.substring(0, 4).contentEquals("[^0-9]"))
            return "";
        newDate = newDate.concat(date.substring(0, 4));
        logger.info("Year transformed: " + newDate);
        newDate = newDate.concat(".");
        if (date.charAt(5) < '0' || date.charAt(5) > '9')
            return "";
        if (date.charAt(6) < '0' || date.charAt(6) > '9') {
            String newMonth = "0" + date.charAt(5);
            newDate = newDate.concat(newMonth);
            shorterMonth = 1;
        }
        else {
            newDate = newDate.concat(date.substring(5, 7));
        }
        logger.info("Month transformed: " + newDate);
        newDate = newDate.concat(".");
        if (date.charAt(8 - shorterMonth) < '0' || date.charAt(8 - shorterMonth) > '9')
            return "";
        if (date.length() < 10 - shorterMonth || date.charAt(9 - shorterMonth) < '0' || date.charAt(9 - shorterMonth) > '9') {
            String newDay = "0" + date.charAt(7);
            newDate = newDate.concat(newDay);
        }
        else {
            newDate = newDate.concat(date.substring(8 - shorterMonth, 10 - shorterMonth));
        }
        logger.info("Day transformed: " + newDate);
        
        return newDate;
    }
}
