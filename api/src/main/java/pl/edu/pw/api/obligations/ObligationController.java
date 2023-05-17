package pl.edu.pw.api.obligations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.ExpenseSplitter;
import pl.edu.pw.GraphLogic;
import pl.edu.pw.api.obligations.dto.*;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/obligations")
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
						obligationWithIdDTO.setAmount(obligation.getAmount());
						obligationWithIdDTO.setStatus(obligation.getStatus());
						obligationWithIdDTO.setTimestamp(obligation.getTimestamp());
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getName());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getName());
						obligationWithIdDTO.setDescription(obligation.getDescription());
						return obligationWithIdDTO;
					})
					.collect(Collectors.toList());
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
		return null;
	}

	@GetMapping("/user/{id}/obligationto")
	public ObligationsToDTO getObligationsTo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		//inni mi wisza
		if(jwtService.checkUserToken(id, request)) {
			List<Obligation> obligations = dbc.findUserById(id).getIsOwed();
			ObligationsToDTO obligationsToDTO = new ObligationsToDTO();
			obligationsToDTO.setObligations(obligations.stream()
					.map(obligation -> {
						ObligationWithIdDTO obligationWithIdDTO = new ObligationWithIdDTO();
						obligationWithIdDTO.setId(obligation.getId());
						obligationWithIdDTO.setAmount(obligation.getAmount());
						obligationWithIdDTO.setStatus(obligation.getStatus());
						obligationWithIdDTO.setTimestamp(obligation.getTimestamp());
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getName());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getName());
						obligationWithIdDTO.setDescription(obligation.getDescription());
						return obligationWithIdDTO;
					})
					.collect(Collectors.toList()));
			Double total = 0.0;
			for (Obligation ob:
					obligations) {
				total += ob.getAmount();
			}
			obligationsToDTO.setTotal(total);
			return obligationsToDTO;
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
		return null;
	}

	@PostMapping("/user/{id}/requestObligation/{fromid}")
	public void requestObligationFrom(@PathVariable Long id, @RequestBody ObligationDTO obligationDTO, HttpServletRequest request, @PathVariable("fromid") Long fromId, HttpServletResponse response) throws IOException {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.requestObligationFrom(dbc.findUserById(fromId), obligationDTO.getAmount(), obligationDTO.getDescription(), obligationDTO.getTimestamp());
			dbc.updateUser(user);
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}

	@GetMapping("/user/{id}/acceptoblication/{toid}")
	public void acceptObligation(@PathVariable Long id, HttpServletRequest request, @PathVariable("toid") Long toId, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			Obligation obligation = user.acceptObligationTo(user,toId);
			dbc.updateUser(user);
			gl.debtTransfer(obligation);
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}

	@GetMapping("/user/{id}/pending") // Nie wiem czy dziala
	public List<ObligationWithIdDTO> getPendingObligations(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Obligation> obligations = user.getPendingObligations();
			return obligations.stream()
					.map(obligation -> {
						ObligationWithIdDTO obligationDTO = new ObligationWithIdDTO();
						obligationDTO.setId(obligation.getId());
						obligationDTO.setAmount(obligation.getAmount());
						obligationDTO.setStatus(obligation.getStatus());
						obligationDTO.setCreditorId(obligation.getCreditor().getName());
						obligationDTO.setDebtorId(obligation.getDebtor().getName());
						obligationDTO.setDescription(obligation.getDescription());
						obligationDTO.setTimestamp(obligation.getTimestamp());
						return obligationDTO;
					})
					.collect(Collectors.toList());
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
		return null;
	}

	@GetMapping("/user/{id}/getobligation/{withid}")
	public List<ObligationWithIdDTO> getObligation(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, @PathVariable("withid") Long withId) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Obligation> obligations = user.getOwes();
			for (Obligation ob :
					user.getIsOwed()) {
				obligations.add(ob);
			}
			return obligations.stream()
					.filter(obl -> (obl.getCreditor().getId()==withId)||(obl.getDebtor().getId()==withId))
					.map(obligation -> {
						ObligationWithIdDTO obligationWithIdDTO = new ObligationWithIdDTO();
						obligationWithIdDTO.setId(obligation.getId());
						obligationWithIdDTO.setId(obligation.getId());
						obligationWithIdDTO.setAmount(obligation.getAmount());
						obligationWithIdDTO.setStatus(obligation.getStatus());
						obligationWithIdDTO.setTimestamp(obligation.getTimestamp());
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getName());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getName());
						obligationWithIdDTO.setDescription(obligation.getDescription());
						return obligationWithIdDTO;
					})
					.collect(Collectors.toList());
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
		return null;
	}

	@PostMapping("/user{id}/split")
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
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}

	@PutMapping("/user/{id}/split/manual")
	public void splitObligationManually(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody SplitObligationManualDTO obligationManualDTO, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			ExpenseSplitter es = new ExpenseSplitter(user, dbc);
			User somePayer;
			Map<User, Double> payers = new HashMap<>();
			try {
				for (Map.Entry<Long, Double> entry : obligationManualDTO.getUsers().entrySet()) {
					somePayer = dbc.findUserById(entry.getKey());
					if(somePayer!=null) payers.put(somePayer, entry.getValue());
					else throw new NoSuchElementException();
				}
				es.split(payers);
			}catch (Exception e){
				System.out.println("Brak conajmniej 1 użytkownika o danym id! Został pomminiety.");
			}

		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}
}
