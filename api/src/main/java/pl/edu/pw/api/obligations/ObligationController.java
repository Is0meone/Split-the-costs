package pl.edu.pw.api.obligations;

import org.springframework.web.bind.annotation.*;
import pl.edu.pw.api.obligations.dto.*;

import java.util.List;

@RestController("/obligations")
public class ObligationController {
	@GetMapping("/user/{id}")
	public List<ObligationWithIdDTO> getObligationsFor(@PathVariable Long id) {
		return null;
	}
	@GetMapping("/to/{id}")
	public ObligationsToDTO getObligationsTo(@PathVariable Long id) {
		return null;
	}
	@PutMapping("/user/{id}")
	public void requestObligationFrom(@PathVariable Long id, @RequestBody ObligationDTO obligationDTO) {

	}
	@PostMapping("/{id}")
	public void acceptObligation(@PathVariable Long id) {

	}

	/**
	 * return the amounts ultimately owed to all users
	 * @return
	 */
	@GetMapping("/total")
	public List<ObligationTotalDTO> getObligationTotals() {
		return null;
	}
	@GetMapping("/pending")
	public List<ObligationWithIdDTO> getPendingObligations() {
		return null;
	}
	@GetMapping("/{id}")
	public ObligationWithIdDTO getObligation(@PathVariable Long id) {
		return null;
	}

	@PutMapping("/split")
	public void splitObligationEqually(@RequestBody SplitObligationDTO obligationDTO) {

	}
	@PutMapping("/split/manual")
	public void splitObligationManually(@RequestBody SplitObligationManualDTO obligationManualDTO) {

	}
}
