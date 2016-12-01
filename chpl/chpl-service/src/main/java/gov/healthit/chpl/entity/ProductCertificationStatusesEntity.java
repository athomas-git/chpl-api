package gov.healthit.chpl.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/** 
 * Object mapping for hibernate-handled table: product_certification_statuses.
 * Table to store counts of certified products per certification status for each product_id
 *
 * @author autogenerated / dlucas
 */
@Entity
@Table(name = "product_certification_statuses")
public class ProductCertificationStatusesEntity implements Cloneable, Serializable{
	/** Serial Version UID. */
	private static final long serialVersionUID = -1396979001199564864L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "product_id", nullable = false)
	private Long id;
	
	@Column(name = "active", nullable = false)
	private Integer active;
	
	@Column(name = "retired", nullable = false)
	private Integer retired;
	
	@Column(name = "withdrawn_by_developer", nullable = false)
	private Integer withdrawnByDeveloper;
	
	@Column(name = "withdrawn_by_acb", nullable = false)
	private Integer withdrawnByAcb;
	
	@Column(name = "suspended_by_acb", nullable = false)
	private Integer suspendedByAcb;
	
	@Column(name = "suspended_by_onc", nullable = false)
	private Integer suspendedByOnc;
	
	@Column(name = "terminated_by_onc", nullable = false)
	private Integer terminatedByOnc;
	
	public ProductCertificationStatusesEntity(){
		
	}
	
	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public ProductCertificationStatusesEntity(Long id) {
		this.id = id;
	}
	
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return ProductCertificationStatusesEntity.class;
	}
	
	/** Constructor taking a given ID.
	 * @param creationDate Date object;
	 * @param deleted Boolean object;
	 * @param id Long object;
	 * @param lastModifiedDate Date object;
	 * @param lastModifiedUser Long object;
	 */
	public ProductCertificationStatusesEntity(Long id, Integer active, Integer retired, Integer withdrawnByDeveloper, Integer withdrawnByAcb, Integer suspendedByAcb) {
		this.id = id;
		this.active = active;
		this.retired = retired;
		this.withdrawnByDeveloper = withdrawnByDeveloper;
		this.withdrawnByAcb = withdrawnByAcb;
		this.suspendedByAcb = suspendedByAcb;
	}
	
	/**
	 * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
	public Long getId(){
		return this.id;
	}
	
	 /**  
	 * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	public void setId(final Long id){
		this.id = id;
	}
	
	/**
	 * Return the value associated with the column: active.
	 * @return A Integer object (this.active)
	 */
	public Integer getActive(){
		return this.active;
	}
	
	/**  
	 * Set the value related to the column: active.
	 * @param active - the aggregate count of active certification_statuses
	 */
	public void setActive(Integer active){
		this.active = active;
	}
	
	/**
	 * Return the value associated with the column: retired.
	 * @return A Integer object (this.retired)
	 */
	public Integer getRetired(){
		return this.retired;
	}
	
	/**  
	 * Set the value related to the column: retired.
	 * @param retired - the aggregate count of retired certification_statuses
	 */
	public void setRetired(Integer retired){
		this.retired = retired;
	}
	
	/**
	 * Return the value associated with the column: withdrawnByDeveloper.
	 * @return A Integer object (this.withdrawnByDeveloper)
	 */
	public Integer getWithdrawnByDeveloper(){
		return this.withdrawnByDeveloper;
	}
	
	/**  
	 * Set the value related to the column: withdrawnByDeveloper.
	 * @param withdrawnByDeveloper - the aggregate count of withdrawnByDeveloper certification_statuses
	 */
	public void setWithdrawnByDeveloper(Integer withdrawnByDeveloper){
		this.withdrawnByDeveloper = withdrawnByDeveloper;
	}
	
	/**
	 * Return the value associated with the column: withdrawnByAcb.
	 * @return A Integer object (this.withdrawnByAcb)
	 */
	public Integer getWithdrawnByAcb(){
		return this.withdrawnByAcb;
	}
	
	/**  
	 * Set the value related to the column: withdrawnByAcb.
	 * @param withdrawnByAcb - the aggregate count of withdrawnByAcb certification_statuses
	 */
	public void setWithdrawnByAcb(Integer withdrawnByAcb){
		this.withdrawnByAcb = withdrawnByAcb;
	}
	
	/**
	 * Return the value associated with the column: suspendedByAcb.
	 * @return A Integer object (this.suspendedByAcb)
	 */
	public Integer getSuspendedByAcb(){
		return this.suspendedByAcb;
	}
	
	/**  
	 * Set the value related to the column: suspendedByAcb.
	 * @param suspendedByAcb - the aggregate count of suspendedByAcb certification_statuses
	 */
	public void setSuspendedByAcb(Integer suspendedByAcb){
		this.suspendedByAcb = suspendedByAcb;
	}
	
	/** Provides toString implementation.
	 * @see java.lang.Object#toString()
	 * @return String representation of this class.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("id: " + this.getId() + ", ");
		sb.append("active: " + this.getActive() + ", ");
		sb.append("retired: " + this.getRetired() + ", ");
		sb.append("withdrawnByDeveloper: " + this.getWithdrawnByDeveloper() + ", ");
		sb.append("withdrawnByAcb: " + this.getWithdrawnByAcb());
		sb.append("suspendedByAcb: " + this.getSuspendedByAcb());
		return sb.toString();		
	}

	public Integer getSuspendedByOnc() {
		return suspendedByOnc;
	}

	public void setSuspendedByOnc(Integer suspendedByOnc) {
		this.suspendedByOnc = suspendedByOnc;
	}

	public Integer getTerminatedByOnc() {
		return terminatedByOnc;
	}

	public void setTerminatedByOnc(Integer terminatedByOnc) {
		this.terminatedByOnc = terminatedByOnc;
	}
}