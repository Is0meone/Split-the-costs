package pl.edu.pw.api.obligations.dto;

import java.util.List;

public class ObligationsToDTO {
	private Double total;
	private List<ObligationWithIdDTO> obligations;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public List<ObligationWithIdDTO> getObligations() {
		return obligations;
	}

	public void setObligations(List<ObligationWithIdDTO> obligations) {
		this.obligations = obligations;
	}
}
