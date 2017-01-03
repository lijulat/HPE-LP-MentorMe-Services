package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * The custom assigned task data.
 */
@Getter
@Setter
@Entity
public class CustomAssignedTaskData extends CustomAssignedData {

    @Column(name = "task_id")
    private long taskId;

    /**
     * The task.
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;
}

