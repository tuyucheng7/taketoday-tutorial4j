package tuyucheng.security

@Grab("spring-boot-starter-security")

@RestController
class SampleController {

	@RequestMapping("/")
	def example() {
		[message: "Hello World!"]
	}
}