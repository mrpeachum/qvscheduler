/**
 * 
 */
package se.mrpeachum.scheduler.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import se.mrpeachum.scheduler.entities.Position;
import se.mrpeachum.scheduler.entities.User;
import se.mrpeachum.scheduler.exception.RedirectException;
import se.mrpeachum.scheduler.service.SchedulerService;

/**
 * @author eolsson
 * 
 */
@Controller
public class ScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

    private static final DateFormat YEAR_WEEK_FORMAT = new SimpleDateFormat("YYYYww");

    private SchedulerService schedulerService;
    
    @Autowired
    public ScheduleController(SchedulerService schedulerService) {
    	this.schedulerService = schedulerService;
    }

    @RequestMapping("/")
    public String getMain(ModelMap model, HttpSession session, @RequestParam(value = "w", required = false) String yearAndWeek) {
    	User user;
    	try {
    		user = schedulerService.fetchOrSaveUser(session);
    	} catch (RedirectException r) {
    		return r.getRedirectUrl();
    	}
        
        model.addAttribute("user", user);
        model.addAttribute("positions", schedulerService.getPositions(user));
        model.addAttribute("firstDayOfWeek", getFirstDayOfWeek(yearAndWeek));
        model.addAttribute("nextWeek", makeWeekLink(yearAndWeek, 1));
        model.addAttribute("previousWeek", makeWeekLink(yearAndWeek, -1));
        return "main";
    }

    @RequestMapping(value ="/positions", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putPositions(@RequestBody List<Position> positions, HttpSession session) {
    	User user;
    	try {
    		user = schedulerService.fetchOrSaveUser(session);
    	} catch (RedirectException r) {
    		throw new IllegalStateException("Must be logged in");
    	}
    	LOGGER.debug("Received {}", positions);
    	
    }
    
    protected final String makeWeekLink(String yearAndWeek, int increment) {
        final Date firstDay = getFirstDayOfWeek(yearAndWeek);
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDay);
        cal.add(Calendar.WEEK_OF_YEAR, increment);
        return YEAR_WEEK_FORMAT.format(cal.getTime());
    }

    protected final Date getFirstDayOfWeek(String yearAndWeek) {
        final Calendar cal = Calendar.getInstance();
        if (yearAndWeek != null) {
            try {
                cal.setTime(YEAR_WEEK_FORMAT.parse(yearAndWeek));
            } catch (ParseException e) {
                LOGGER.error("Failed to parse the year and week. Using now. {}", e);
                cal.setTime(new Date());
            }
        } else {
            cal.setTime(new Date());
        }
        
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }


}
