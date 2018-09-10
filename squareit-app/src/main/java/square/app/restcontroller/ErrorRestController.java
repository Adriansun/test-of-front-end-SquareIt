package square.app.restcontroller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorRestController implements ErrorController {

  /*
   * If a user enter a URL on the site to a page that does not exist then the user will be redirected to the 404 page.
   */
  @GetMapping(value = "/error")
  public String defaultErrorMessage() {
    return "redirect:error/404.html";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
