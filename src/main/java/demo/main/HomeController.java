package demo.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Maps the root URL to the index (home) page.
 * 
 * @author Paul Chapman
 */
@Controller
public class HomeController {

	@RequestMapping("/")
	public String home() {
		return "index";
	}
 }
