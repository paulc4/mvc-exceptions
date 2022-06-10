package demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.config.ResponseDataControllerAdvice;

/**
 * Maps URLs directly to views. Unlike View Controllers, using an actual
 * Controller causes the {@link ResponseDataControllerAdvice} to be invoked and
 * setup model data needed by these views.
 * 
 * @author Paul Chapman
 */
@Controller
public class ViewMappingController {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/unannotated")
	public String unannotated() {
		return "unannotated";
	}

	@GetMapping("/no-handler")
	public String noHandler() {
		return "no-handler";
	}
	
	@GetMapping("/demo5")
	public String demo4() {
		return "demo5";
	}

}
