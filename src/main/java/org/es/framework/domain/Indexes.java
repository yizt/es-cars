package org.es.framework.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.es.framework.utils.EsConstants;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 指标
 * @author zhouqi
 *
 */
@Entity
@Table(name = "indexes")
public class Indexes extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 指标名称
	 */
	@Column(length = 200)
	private String name;

	/**
	 * 指标编码
	 */
	@Column(length = 40)
	private String code;

	/**
	 * 指标层级
	 */
	@Column(name = "index_layer")
	private Integer indexLayer = EsConstants.MenuLevel.root;

	/**
	 * 是否叶子节点;1-是,0-否;非叶子节点为指标分类，叶子节点为指标名称
	 */
	@NotEmpty
	@Column(name = "is_leaf")
	private Integer isLeaf;

	/**
	 * 备注描述
	 */
	@Column(length = 2000, name = "memo")
	private String memo;

	/**
	 * 父类
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "p_index_id", foreignKey = @ForeignKey(name = "indexes_p_index_id_fkey"))
	private Indexes pIndexes;

	public Integer getIndexLayer() {
		return indexLayer;
	}

	public void setIndexLayer(Integer indexLayer) {
		this.indexLayer = indexLayer;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Indexes getpIndexes() {
		return pIndexes;
	}

	public void setpIndexes(Indexes pIndexes) {
		this.pIndexes = pIndexes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
