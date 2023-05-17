package pl.edu.pw.api.obligations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.ExpenseSplitter;
import pl.edu.pw.GraphLogic;
import pl.edu.pw.api.friendship.dto.FriendshipRequestDTO;
import pl.edu.pw.api.obligations.dto.*;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController("/obligations")
public class ObligationController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector(1);
	private GraphLogic gl = new GraphLogic(dbc);
	@GetMapping("/user/{id}/obligationwith")
	public List<ObligationWithIdDTO> getObligationsFor(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// ja wisze
		if(jwtService.checkUserToken(id, request)) {
			List<Obligation> obligations = dbc.findUserById(id).getOwes();
			return obligations.stream()
					.map(obligation -> {
						ObligationWithIdDTO obligationWithIdDTO = new ObligationWithIdDTO();
						obligationWithIdDTO.setId(obligation.getId());
						return obligationWithIdDTO;
					})
					.collect(Collectors.toList());
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
		return null;
	}

	@GetMapping("/to/{id}/obligationto")
	public ObligationsToDTO getObligationsTo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		//inni mi wisza
		if(jwtService.checkUserToken(id, request)) {
			List<Obligation> obligations = dbc.findUserById(id).getIsOwed();
			ObligationsToDTO obligationsToDTO = new ObligationsToDTO();
			obligationsToDTO.setObligations(obligations.stream()
					.map(obligation -> {
						ObligationWithIdDTO obligationWithIdDTO = new ObligationWithIdDTO();
						obligationWithIdDTO.setId(obligation.getId());
						return obligationWithIdDTO;
					})
					.collect(Collectors.toList()));
			return obligationsToDTO;
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
		return null;
	}

	@PutMapping("/user/{id}/requestObligation/{fromid}")
	public void requestObligationFrom(@PathVariable Long id, @RequestBody ObligationDTO obligationDTO, HttpServletRequest request, @PathVariable("fromid") Long fromId, HttpServletResponse response) throws IOException {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.requestObligationFrom(dbc.findUserById(fromId), obligationDTO.getAmount());
			dbc.updateUser(user);
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
	}
	@PostMapping("/user/{id}/acceptoblication/{toid}")
	public void acceptObligation(@PathVariable Long id, HttpServletRequest request, @PathVariable("toid") Long toId, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.acceptObligationTo(user,toId);
			dbc.updateUser(user);
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
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
	public List<ObligationWithIdDTO> getPendingObligations(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
		}else {
			response.getWriter().print("Wrong token");
			response.setStatus(401);
		}
		return null;
	}

	@GetMapping("/user/{id}/getObligation")
	public ObligationWithIdDTO getObligation(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			Obligation obligation = new Obligation();
			//TODO:
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
		return null;
	}

	@PutMapping("/user{id}/split")
	public void splitObligationEqually(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody SplitObligationDTO obligationDTO, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			User somePayer;
			List<User> payers = new ArrayList<>();
			try {
				for (Long usersId : obligationDTO.getUsers()) {
					somePayer = dbc.findUserById(usersId);
					if (somePayer != null) {
						payers.add(somePayer);
					} else throw new NoSuchElementException();
				}
			}catch (Exception e){
				System.out.println("Brak conajmniej 1 użytkownika o danym id! Został pomminiety, a kwota podzielona przez pozostalych uzytownikow");
			}
			ExpenseSplitter expenseSplitter = new ExpenseSplitter(user,dbc);
			expenseSplitter.split(obligationDTO.getAmount(), payers);
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
	}
	@PutMapping("/user/{id}/split/manual")
	public void splitObligationManually(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody SplitObligationManualDTO obligationManualDTO, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			//TODO:
		}else {
			response.getWriter().print("Invalid Token");
			response.setStatus(401);
		}
	}
}
