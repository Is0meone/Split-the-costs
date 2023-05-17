package pl.edu.pw.api.obligations;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.ExpenseSplitter;
import pl.edu.pw.api.obligations.dto.*;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.api.users.dto.UserDTO;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/obligations")
public class ObligationController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector(1);
	@GetMapping("/user/{id}")
	public List<ObligationWithIdDTO> getObligationsFor(@PathVariable Long id) {
		User user = dbc.findUserById(id);
		//TODO:
		return null;
	}

	@GetMapping("/to/{id}")
	public ObligationsToDTO getObligationsTo(@PathVariable Long id) {
		//TODO:
		return null;
	}

	@PutMapping("/user/{id}/requestObligation/{fromid}")
	public void requestObligationFrom(@PathVariable Long id, @RequestBody ObligationDTO obligationDTO, HttpServletRequest request, @PathVariable("fromid") Long fromId) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.requestObligationFrom(dbc.findUserById(fromId), obligationDTO.getAmount());
			dbc.updateUser(user);
		}
	}
	@PostMapping("/user/{id}/acceptoblication/{toid}")
	public void acceptObligation(@PathVariable Long id, HttpServletRequest request, @PathVariable("toid") Long toId) {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.acceptObligationTo(user,toId);
			dbc.updateUser(user);
		}
	}
	/**
	 * return the amounts ultimately owed to all users
	 * @return
	 */
	@GetMapping("/user/{id}/total")
	public List<ObligationTotalDTO> getObligationTotals() {
		//TODO:
		return null;
	}

	@GetMapping("/user/{id}/pending") // Nie wiem czy dziala
	public List<ObligationWithIdDTO> getPendingObligations(@PathVariable Long id, HttpServletRequest request) {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Obligation> obligations = user.getPendingObligations();
			return obligations.stream()
					.map(u -> {
						ObligationWithIdDTO obligationDTO = new ObligationWithIdDTO();
						obligationDTO.setId(user.getId());
						obligationDTO.setAmount(u.getAmount());
						obligationDTO.setCreditorId(u.getCreditor().getName());
						obligationDTO.setDebtorId(u.getDebtor().getName());
						obligationDTO.setDescription(u.getDescription());
						obligationDTO.setTimestamp(u.getTimestamp().toString());
						return obligationDTO;
					})
					.collect(Collectors.toList());
		}
		return null;
	}

	@GetMapping("/user/{id}/getObligation")
	public ObligationWithIdDTO getObligation(@PathVariable("id") Long id, HttpServletRequest request) {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			Obligation obligation = new Obligation();
			//TODO:
		}
		return null;
	}

	@PutMapping("/user{id}/split")
	public void splitObligationEqually(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody SplitObligationDTO obligationDTO) {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			ExpenseSplitter expenseSplitter = new ExpenseSplitter(user,dbc);
//			expenseSplitter.split();
			//TODO:
		}
	}
	@PutMapping("/user/{id}/split/manual")
	public void splitObligationManually(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody SplitObligationManualDTO obligationManualDTO) {
		if (jwtService.checkUserToken(id, request)) {
			//TODO:
		}
	}
}
