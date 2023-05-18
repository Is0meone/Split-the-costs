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
	private DBConnector dbc = new DBConnector("1");
	private GraphLogic gl = new GraphLogic(dbc);

	@GetMapping("/user/{id}/debts")
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
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getId());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getId());
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

	@GetMapping("/user/{id}/credits")
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
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getId());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getId());
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

	@PostMapping("/user/{id}/request")
	public void requestObligationFrom(@PathVariable Long id, @RequestBody ObligationDTO obligationDTO, HttpServletRequest request,HttpServletResponse response) throws IOException {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			if(user!=null){
				Optional<Obligation> optional = user.requestObligationFrom(dbc.findUserById(obligationDTO.getDebtorId()), obligationDTO.getAmount(), obligationDTO.getDescription(), obligationDTO.getTimestamp());
			if(optional.isPresent()){
				dbc.addObligation(optional.get());

				if(user.isSuperFriend(dbc.findUserById(obligationDTO.getDebtorId()))) {
					gl.debtTransfer(dbc.findObligationBetweenUsers(dbc.findUserById(id), dbc.findUserById(obligationDTO.getDebtorId())));
				}
			}else{
				response.getWriter().print("No such user!");
				response.setStatus(420);
			}
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}}

	@GetMapping("/user/{id}/accept/{toid}/{oblid}")
	public void acceptObligation(@PathVariable Long id, HttpServletRequest request, @PathVariable("toid") Long toId, @PathVariable("oblid") Long oblid, HttpServletResponse response) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			User payer = dbc.findUserById(toId);
			Obligation obligation = user.acceptObligationTo(payer, oblid);
			if(obligation!=null) {
				dbc.addObligation(obligation);
				gl.debtTransfer(obligation);
			}
			else{
				response.getWriter().print("Wrong input provided: no such user or obligation!");
				response.setStatus(401);
			}
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
						obligationDTO.setCreditorId(obligation.getCreditor().getId());
						obligationDTO.setDebtorId(obligation.getDebtor().getId());
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

	@GetMapping("/user/{id}/getwith/{withid}")
	public List<ObligationDTO> getObligationWith(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, @PathVariable("withid") Long withId) throws IOException {
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
						obligationWithIdDTO.setAmount(obligation.getAmount());
						obligationWithIdDTO.setStatus(obligation.getStatus());
						obligationWithIdDTO.setTimestamp(obligation.getTimestamp());
						obligationWithIdDTO.setCreditorId(obligation.getCreditor().getId());
						obligationWithIdDTO.setDebtorId(obligation.getDebtor().getId());
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


	@GetMapping("/user/{id}/getbyid/{withid}")
	public List<ObligationWithIdDTO> getObligationById(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, @PathVariable("withid") Long withId) throws IOException {
		if (jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Obligation> obl = user.getIsOwed();
			obl.addAll(user.getOwes());
			if(obl!=null) {
				return obl.stream()
						.filter(oblig -> (oblig.getId() == withId))
						.map(obligation -> {
							ObligationWithIdDTO obligationWithIdDTO = new ObligationWithIdDTO();
							obligationWithIdDTO.setId(obligation.getId());
							obligationWithIdDTO.setAmount(obligation.getAmount());
							obligationWithIdDTO.setStatus(obligation.getStatus());
							obligationWithIdDTO.setTimestamp(obligation.getTimestamp());
							obligationWithIdDTO.setCreditorId(obligation.getCreditor().getId());
							obligationWithIdDTO.setDebtorId(obligation.getDebtor().getId());
							obligationWithIdDTO.setDescription(obligation.getDescription());
							return obligationWithIdDTO;
						})
						.collect(Collectors.toList());
			}
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
		return null;
	}



	@PostMapping("/user/{id}/split")
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
			response.getWriter().print("Obligation splitted!");
		}else {
			response.getWriter().print("Access Denied");
			response.setStatus(401);
		}
	}

	@PostMapping("/user/{id}/split/manual")
	public void splitObligationManually(@PathVariable("id") Long id, HttpServletRequest request,
										@RequestBody SplitObligationManualDTO obligationManualDTO, HttpServletResponse response) throws IOException {
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
