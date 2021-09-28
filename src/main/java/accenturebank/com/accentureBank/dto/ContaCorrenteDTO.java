package accenturebank.com.accentureBank.dto;

import java.io.Serializable;

import accenturebank.com.accentureBank.domain.ContaCorrente;

public class ContaCorrenteDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long idAgencia;
	private Long idCliente;

	public ContaCorrenteDTO() {

	}

	public ContaCorrenteDTO(ContaCorrente obj) {
		this.id = obj.getId();
		this.idAgencia = obj.getAgencia().getId();
		this.idCliente = obj.getCliente().getId();

	}

	public Long getIdAgencia() {
		return idAgencia;
	}

	public void setIdAgencia(Long idAgencia) {
		this.idAgencia = idAgencia;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}