package cn.tuyucheng.taketoday.thymeleaf.expression;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
public class DinoController {

	ArrayList<Dino> dinos = new ArrayList<Dino>();

	@RequestMapping("/")
	public String dinoList(Model model) {
		Dino dinos = new Dino(1, "alpha", "red", "50kg");

		model.addAttribute("dinos", new Dino());
		model.addAttribute("dinos", dinos);
		System.out.println(dinos);

		return "templates-3/index";
	}

	@RequestMapping("/create")
	public String dinoCreate(Model model) {
		model.addAttribute("dinos", new Dino());
		return "templates-3/form";
	}

	@PostMapping("/dino")
	public String dinoSubmit(@ModelAttribute Dino dino, Model model) {
		model.addAttribute("dino", dino);
		return "templates-3/result";
	}
}