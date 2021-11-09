import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request) {

        return "Error message";
    }

}
