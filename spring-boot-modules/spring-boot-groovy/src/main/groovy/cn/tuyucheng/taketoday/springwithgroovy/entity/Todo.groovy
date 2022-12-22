package cn.tuyucheng.taketoday.springwithgroovy.entity

import javax.persistence.*

@Entity
@Table(name = 'todo')
class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id

	@Column
	String task

	@Column
	Boolean isCompleted
}