-- -----------------------------------------------------
-- Table `locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `country`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `country` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `state`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `state` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  `country_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
   CONSTRAINT `start_country_id_fk`
      FOREIGN KEY (`country_id`)
      REFERENCES `country` (`id`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(256) NOT NULL,
  `first_name` VARCHAR(256) NULL,
  `last_name` VARCHAR(256) NULL,
  `email` VARCHAR(256) NOT NULL,
  `profile_picture_path` VARCHAR(512) NULL,
  `created_on` DATETIME NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `provider_id` VARCHAR(256) NULL,
  `provider_user_id` VARCHAR(256) NULL,
  `access_token` VARCHAR(256) NULL,
  `is_virtual_user` TINYINT(1) NOT NULL,
  `is_agreed_agreement` TINYINT(1) NOT NULL,
  `street_address` VARCHAR (512),
  `city` VARCHAR(128),
  `state_id` BIGINT NULL,
  `country_id` BIGINT NULL,
  `postal_code` VARCHAR(25),
  `longitude` DECIMAL (16, 8),
  `latitude` DECIMAL (16, 8),
  `last_modified_on` DATETIME NOT NULL,
  `last_login_on` DATETIME,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_email` (`email`),
  CONSTRAINT `user_state_id_fk`
    FOREIGN KEY (`state_id`)
    REFERENCES `state` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_country_id_fk`
    FOREIGN KEY (`country_id`)
    REFERENCES `country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `institution_admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_admin` (
  `id` BIGINT NOT NULL,
  `institution_id` BIGINT NOT NULL,
  `title` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `i_a_u_fk`
    FOREIGN KEY (`id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institution`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `institution_name` VARCHAR(256) NOT NULL,
  `parent_organization` VARCHAR(256) NULL,
  `street_address` VARCHAR(256) NOT NULL,
  `city` VARCHAR(128) NOT NULL,
  `state_id` BIGINT NULL,
  `zip` VARCHAR(45) NOT NULL,
  `country_id` BIGINT NOT NULL,
  `phone` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `logo_path` VARCHAR(512) NULL,
  `created_on` DATETIME NOT NULL,
  `last_modified_on` DATETIME NOT NULL,
  `default_institution` TINYINT(1) NOT NULL default 0,
  PRIMARY KEY (`id`),
  INDEX `i_c_idx` (`country_id` ASC),
  INDEX `i_s_idx` (`state_id` ASC),
  CONSTRAINT `i_c`
    FOREIGN KEY (`country_id`)
    REFERENCES `country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `i_s`
    FOREIGN KEY (`state_id`)
    REFERENCES `state` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `institution_contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_contact` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(256) NOT NULL,
  `first_name` VARCHAR(256) NOT NULL,
  `last_name` VARCHAR(256) NOT NULL,
  `email` VARCHAR(256) NOT NULL,
  `phone_number` VARCHAR(512) NULL,
  `primary_contact` TINYINT(1) NOT NULL,
  `institution_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ic_i_fk_idx` (`institution_id` ASC),
  CONSTRAINT `ic_i_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institutional_program`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institutional_program` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `program_name` VARCHAR(128) NOT NULL,
  `description` VARCHAR(4096) NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `institution_id` BIGINT NOT NULL,
  `duration_in_days` INT NOT NULL,
  `created_on` DATETIME NOT NULL,
  `program_image_url` VARCHAR(256) NULL,
  `last_modified_on` DATETIME NOT NULL,
  `locale_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ip_i_fk_idx` (`institution_id` ASC),
  INDEX `ip_l_fk_idx` (`locale_id` ASC),
    CONSTRAINT `ip_l_fk`
    FOREIGN KEY (`locale_id`)
    REFERENCES `locale` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
    CONSTRAINT `ip_i_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `goal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `goal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `number` INT NOT NULL,
  `subject` VARCHAR(256) NULL,
  `description` VARCHAR(1024) NULL,
  `duration_in_days` INT NOT NULL,
  `institutional_program_id` BIGINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `parent_consent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `parent_consent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_name` VARCHAR(1024) NOT NULL,
  `signature_file_path` VARCHAR(1024) NOT NULL,
  `parent_email` VARCHAR(64) NULL,
  `parent_phone` VARCHAR(64) NULL,
  `token` VARCHAR(128) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institution_affiliation_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_affiliation_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(45) NOT NULL,
  `institution_id` BIGINT NOT NULL,
  `used` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `iac_i_fk_idx` (`institution_id` ASC),
  CONSTRAINT `iac_i_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee` (
  `id` BIGINT NOT NULL,
  `institution_id` BIGINT NOT NULL,
  `birth_date` DATE NOT NULL,
  `phone` VARCHAR(45) NULL,
  `skype_username` VARCHAR(45) NULL,
  `intro_video_link` VARCHAR(512) NULL,
  `description` VARCHAR(1024) NULL,
  `average_performance_score` INT NULL,
  `family_income` DECIMAL(19,2) NULL,
  `school` VARCHAR(1028) NULL,
  `institution_affiliation_code_id` BIGINT NULL,
  `parent_consent_id` BIGINT NULL,
  `facebook_url` VARCHAR(256) NULL,
  `whats_app_name` VARCHAR(256) NULL,
  PRIMARY KEY (`id`),
  INDEX `me_pk_fk_idx` (`parent_consent_id` ASC),
  INDEX `me_iac_fk_idx` (`institution_affiliation_code_id` ASC),
  CONSTRAINT `me_id_fk`
    FOREIGN KEY (`id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
   CONSTRAINT `me_ii_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `me_u_fk`
    FOREIGN KEY (`id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `me_pk_fk`
    FOREIGN KEY (`parent_consent_id`)
    REFERENCES `parent_consent` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `me_iac_fk`
    FOREIGN KEY (`institution_affiliation_code_id`)
    REFERENCES `institution_affiliation_code` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentor` (
  `id` BIGINT NOT NULL,
  `institution_id` BIGINT NOT NULL,
  `birth_date` DATE NULL,
  `phone` VARCHAR(45) NULL,
  `skype_username` VARCHAR(45) NULL,
  `intro_video_link` VARCHAR(512) NULL,
  `description` VARCHAR(1024) NULL,
  `average_performance_score` INT NULL,
  `mentor_type` VARCHAR(45) NULL,
  `company_name` VARCHAR(45) NULL,
  `linked_in_url` VARCHAR(256) NULL,
  `whats_app_name` VARCHAR(256) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `m_id_fk`
    FOREIGN KEY (`id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `m_ii_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `m_u_fk`
    FOREIGN KEY (`id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mentor_score` INT NULL,
  `comment` VARCHAR(45) NULL,
  `created_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentor_feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentor_feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mentee_score` INT NULL,
  `comment` VARCHAR(45) NULL,
  `created_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_mentor_program`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_program` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mentee_id` BIGINT NOT NULL,
  `mentor_id` BIGINT NOT NULL,
  `institutional_program_id` BIGINT NOT NULL,
  `request_status` VARCHAR(45) NULL,
  `mentee_feedback_id` BIGINT NULL,
  `mentor_feedback_id` BIGINT NULL,
  `start_date` DATE NULL,
  `end_date` DATE NULL,
  `completed` TINYINT(1) NOT NULL,
  `completed_on` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `mmp_me_idx` (`mentee_id` ASC),
  INDEX `mmp_m_fk_idx` (`mentor_id` ASC),
  INDEX `mmp_ip_idx` (`institutional_program_id` ASC),
  INDEX `mmp_mef_fk_idx` (`mentee_feedback_id` ASC),
  INDEX `mmp_mf_fk_idx` (`mentor_feedback_id` ASC),
  CONSTRAINT `mmp_me`
    FOREIGN KEY (`mentee_id`)
    REFERENCES `mentee` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mmp_m_fk`
    FOREIGN KEY (`mentor_id`)
    REFERENCES `mentor` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mmp_ip`
    FOREIGN KEY (`institutional_program_id`)
    REFERENCES `institutional_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mmp_mef_fk`
    FOREIGN KEY (`mentee_feedback_id`)
    REFERENCES `mentee_feedback` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mmp_mf_fk`
    FOREIGN KEY (`mentor_feedback_id`)
    REFERENCES `mentor_feedback` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_mentor_goal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_goal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goal_id` BIGINT NULL,
  `mentee_mentor_program_id` BIGINT NOT NULL,
  `completed` TINYINT(1) NOT NULL,
  `completed_on` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `mg_mmp_idx` (`mentee_mentor_program_id` ASC),
  INDEX `mg_g_idx` (`goal_id` ASC),
  CONSTRAINT `mg_mmp`
    FOREIGN KEY (`mentee_mentor_program_id`)
    REFERENCES `mentee_mentor_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mg_g`
    FOREIGN KEY (`goal_id`)
    REFERENCES `goal` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(1024) NOT NULL,
  `duration_in_days` INT NULL,
  `mentee_assignment` TINYINT(1) NULL,
  `mentor_assignment` TINYINT(1) NULL,
  `goal_id` BIGINT NOT NULL,
  `number` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `t_g_fk_idx` (`goal_id` ASC),
  CONSTRAINT `t_g_fk`
    FOREIGN KEY (`goal_id`)
    REFERENCES `goal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_mentor_task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_id` BIGINT NOT NULL,
  `completed` TINYINT(1) NOT NULL,
  `completed_on` DATETIME NULL,
  `mentee_mentor_goal_id` BIGINT NOT NULL,
  `start_date` DATE NULL,
  `end_date` DATE NULL,
  PRIMARY KEY (`id`),
  INDEX `mt_mg_idx` (`mentee_mentor_goal_id` ASC),
  INDEX `mt_t_idx` (`task_id` ASC),
  CONSTRAINT `mt_mg`
    FOREIGN KEY (`mentee_mentor_goal_id`)
    REFERENCES `mentee_mentor_goal` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mt_t`
    FOREIGN KEY (`task_id`)
    REFERENCES `task` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `professional_experience_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professional_experience_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `position` VARCHAR(45) NULL,
  `work_location` VARCHAR(45) NULL,
  `start_date` DATE NULL,
  `end_date` DATE NULL,
  `description` VARCHAR(1024) NULL,
  `mentor_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ped_m_idx` (`mentor_id` ASC),
  CONSTRAINT `ped_m`
    FOREIGN KEY (`mentor_id`)
    REFERENCES `mentor` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `institution_id` BIGINT NOT NULL,
  `name` VARCHAR(256) NULL,
  `description` VARCHAR(1024) NOT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `event_location` VARCHAR(256) NOT NULL,
  `event_location_address` VARCHAR(256) NOT NULL,
  `city` VARCHAR(128) NOT NULL,
  `state_id` BIGINT NULL,
  `zip` VARCHAR(16) NULL,
  `country_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `global` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `i_c_idx` (`country_id` ASC),
  INDEX `i_s_idx` (`state_id` ASC),
  CONSTRAINT `i_ii`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `i_c0`
    FOREIGN KEY (`country_id`)
    REFERENCES `country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `i_s0`
    FOREIGN KEY (`state_id`)
    REFERENCES `state` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;




-- -----------------------------------------------------
-- Table `activity`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `institutional_program_id` BIGINT NULL,
  `activity_type` VARCHAR(45) NULL,
  `object_id` BIGINT NOT NULL,
  `description` VARCHAR(512) NULL,
  `created_by` BIGINT NOT NULL,
  `created_on` DATETIME NOT NULL,
  `mentee_id` BIGINT NULL,
  `mentor_id` BIGINT NULL,
  `global` TINYINT(1) NOT NULL,
  `last_modified_by` BIGINT NOT NULL,
  `last_modified_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `a_me_idx` (`mentee_id` ASC),
  INDEX `a_m_idx` (`mentor_id` ASC),
  CONSTRAINT `a_ip`
    FOREIGN KEY (`institutional_program_id`)
    REFERENCES `institutional_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `a_me`
    FOREIGN KEY (`mentee_id`)
    REFERENCES `mentee` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `a_m`
    FOREIGN KEY (`mentor_id`)
    REFERENCES `mentor` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institution_agreement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_agreement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `agreement_name` VARCHAR(128) NOT NULL,
  `agreement_file_path` VARCHAR(512) NOT NULL,
  `institution_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `ia_ii_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institution_agreement_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_agreement_user_role` (
  `institution_agreement_id` BIGINT NOT NULL,
  `user_role_id` BIGINT NOT NULL,
  PRIMARY KEY (`institution_agreement_id`, `user_role_id`),
  INDEX `i_a_u_r_u_r_fk_idx` (`user_role_id` ASC),
  CONSTRAINT `i_a_u_r_i_i_fk`
    FOREIGN KEY (`institution_agreement_id`)
    REFERENCES `institution_agreement` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `i_a_u_r_u_r_fk`
    FOREIGN KEY (`user_role_id`)
    REFERENCES `user_role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `user_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_user_role` (
  `user_id` BIGINT NOT NULL,
  `user_role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `user_role_id`),
  INDEX `u_ur_ur_fk_idx` (`user_role_id` ASC),
  CONSTRAINT `u_ur_u_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `u_ur_ur_fk`
    FOREIGN KEY (`user_role_id`)
    REFERENCES `user_role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `personal_interest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personal_interest` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  `picture_path` VARCHAR(512) NULL,
  `parent_category_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `pi_pc_fk_idx` (`parent_category_id` ASC),
  CONSTRAINT `pi_pc_fk`
    FOREIGN KEY (`parent_category_id`)
    REFERENCES `personal_interest` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weighted_personal_interest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weighted_personal_interest` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `personal_interest_id` BIGINT NOT NULL,
  `weight` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `wpi_u_fk_idx` (`user_id` ASC),
  INDEX `wpi_pi_fk_idx` (`personal_interest_id` ASC),
  CONSTRAINT `wpi_u_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `wpi_pi_fk`
    FOREIGN KEY (`personal_interest_id`)
    REFERENCES `personal_interest` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `professional_interest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professional_interest` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  `picture_path` VARCHAR(512) NULL,
  `parent_category_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `pi_pc_fk_idx` (`parent_category_id` ASC),
  CONSTRAINT `pi_pc_ifk`
    FOREIGN KEY (`parent_category_id`)
    REFERENCES `professional_interest` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weighted_professional_interest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weighted_professional_interest` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `professional_interest_id` BIGINT NOT NULL,
  `weight` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `wpi_u_fk_idx` (`user_id` ASC),
  INDEX `wpi_pi_fk_idx` (`professional_interest_id` ASC),
  CONSTRAINT `wpi_u_fk0`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `wpi_pi_fk1`
    FOREIGN KEY (`professional_interest_id`)
    REFERENCES `professional_interest` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `skill`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  `description` VARCHAR(1024) NULL,
  `image_path` VARCHAR(1024) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mentee_skill`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ms_u_fk_idx` (`user_id` ASC),
  INDEX `ms_s_fk_idx` (`skill_id` ASC),
    CONSTRAINT `ms_u_fk0`
        FOREIGN KEY (`user_id`)
        REFERENCES `user` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
      CONSTRAINT `ms_s_fk1`
        FOREIGN KEY (`skill_id`)
        REFERENCES `skill` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `institutional_program_skill`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institutional_program_skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `institutional_program_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ps_ipi_fk_idx1` (`institutional_program_id` ASC),
  INDEX `ps_s_fk_idx1` (`skill_id` ASC),
    CONSTRAINT `ps_ipi_fk10`
        FOREIGN KEY (`institutional_program_id`)
        REFERENCES `institutional_program` (`id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
      CONSTRAINT `ps_s_fk10`
        FOREIGN KEY (`skill_id`)
        REFERENCES `skill` (`id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(512) NOT NULL,
  `path` VARCHAR(512) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_on` DATETIME NOT NULL,
  `last_modified_by` BIGINT NOT NULL,
  `last_modified_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `useful_link`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `useful_link` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(128)NOT NULL,
  `address` VARCHAR(512) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `responsibility`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `responsibility` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `number` INT NOT NULL,
  `title` VARCHAR(128) NOT NULL,
  `date` DATE NOT NULL,
  `mentee_responsibility` TINYINT(1) NULL,
  `mentor_responsibility` TINYINT(1) NULL,
  `institutional_program_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `r_ip_fk_idx` (`institutional_program_id` ASC),
  CONSTRAINT `r_ip_fk`
    FOREIGN KEY (`institutional_program_id`)
    REFERENCES `institutional_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `goal_useful_link`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `goal_useful_link` (
  `goal_id` BIGINT NOT NULL,
  `useful_link_id` BIGINT NOT NULL,
  PRIMARY KEY (`goal_id`, `useful_link_id`),
  INDEX `pul_ul_fk_idx` (`useful_link_id` ASC),
  CONSTRAINT `gul_g_fk0`
    FOREIGN KEY (`goal_id`)
    REFERENCES `goal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `pul_ul_fk0`
    FOREIGN KEY (`useful_link_id`)
    REFERENCES `useful_link` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `goal_document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `goal_document` (
  `goal_id` BIGINT NOT NULL,
  `document_id` BIGINT NOT NULL,
  PRIMARY KEY (`goal_id`, `document_id`),
  INDEX `pd_d_fk0_idx` (`document_id` ASC),
  CONSTRAINT `gd_g_fk0`
    FOREIGN KEY (`goal_id`)
    REFERENCES `goal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `gd_d_fk00`
    FOREIGN KEY (`document_id`)
    REFERENCES `document` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `forgot_password`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forgot_password` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(128) NOT NULL,
  `expired_on` DATETIME NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institution_admin_access_request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institution_admin_access_request` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `status` VARCHAR(45) NOT NULL,
  `institution_admin_id` BIGINT NOT NULL,
  `institution_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `iaar_i_fk_idx` (`institution_id` ASC),
  INDEX `iaar_ia_fk_idx` (`institution_admin_id` ASC),
  CONSTRAINT `iaar_i_fk`
    FOREIGN KEY (`institution_id`)
    REFERENCES `institution` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `iaar_ia_fk`
    FOREIGN KEY (`institution_admin_id`)
    REFERENCES `institution_admin` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `professional_consultant_area`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professional_consultant_area` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;



-- -----------------------------------------------------
-- Table `event_invited_mentor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `event_invited_mentor` (
  `event_id` BIGINT NOT NULL,
  `mentor_id` BIGINT NOT NULL,
  PRIMARY KEY (`event_id`, `mentor_id`),
  CONSTRAINT `e_imto_e_fk`
    FOREIGN KEY (`event_id`)
    REFERENCES `event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `e_imto_m_fk`
    FOREIGN KEY (`mentor_id`)
    REFERENCES `mentor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `event_invited_mentee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `event_invited_mentee` (
  `event_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  PRIMARY KEY (`event_id`, `mentee_id`),
  CONSTRAINT `e_imte_e_fk`
    FOREIGN KEY (`event_id`)
    REFERENCES `event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `e_imte_m_fk`
    FOREIGN KEY (`mentee_id`)
    REFERENCES `mentee` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `institutional_program_document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institutional_program_document` (
  `institutional_program_id` BIGINT NOT NULL,
  `document_id` BIGINT NOT NULL,
  PRIMARY KEY (`institutional_program_id`, `document_id`),
  INDEX `ipd_dfk0_idx` (`document_id` ASC),
  CONSTRAINT `ipd_ip_fk0`
    FOREIGN KEY (`institutional_program_id`)
    REFERENCES `institutional_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `ipd_d_fk00`
    FOREIGN KEY (`document_id`)
    REFERENCES `document` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `institutional_program_link`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `institutional_program_link` (
  `institutional_program_id` BIGINT NOT NULL,
  `useful_link_id` BIGINT NOT NULL,
  PRIMARY KEY (`institutional_program_id`, `useful_link_id`),
  INDEX `ipl_dfk0_idx` (`useful_link_id` ASC),
  CONSTRAINT `ipl_ip_fk0`
    FOREIGN KEY (`institutional_program_id`)
    REFERENCES `institutional_program` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `ipl_l_fk00`
    FOREIGN KEY (`useful_link_id`)
    REFERENCES `useful_link` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `institutional_program_area`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentor_professional_area` (
  `mentor_id` BIGINT NOT NULL,
  `professional_area_id` BIGINT NOT NULL,
  PRIMARY KEY (`mentor_id`, `professional_area_id`),
  INDEX `mpa_pfk0_idx` (`professional_area_id` ASC),
  CONSTRAINT `mpa_m_fk0`
    FOREIGN KEY (`mentor_id`)
    REFERENCES `mentor` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `mpa_pa_fk00`
    FOREIGN KEY (`professional_area_id`)
    REFERENCES `professional_consultant_area` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mentee_mentor_responsibility`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_responsibility` (
`id` BIGINT NOT NULL AUTO_INCREMENT,
`number` INT NOT NULL,
`title` VARCHAR(128) NOT NULL,
`date` DATE NULL,
`mentee_responsibility` TINYINT(1) NULL,
`mentor_responsibility` TINYINT(1) NULL,
`responsibility_id` BIGINT NULL,
`mentee_mentor_program_id` BIGINT NOT NULL,
PRIMARY KEY (`id`),
INDEX `r_mmpi_fk_idx` (`mentee_mentor_program_id` ASC),
CONSTRAINT `r_mmpi_fk`
FOREIGN KEY (`mentee_mentor_program_id`)
REFERENCES `mentee_mentor_program` (`id`)
ON DELETE CASCADE
ON UPDATE NO ACTION,
CONSTRAINT `r_mmri_fk`
FOREIGN KEY (`responsibility_id`)
REFERENCES `responsibility` (`id`)
ON DELETE CASCADE
ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mentee_mentor_program_useful_link`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_program_useful_link` (
  `mentee_mentor_program_id` BIGINT NOT NULL,
  `useful_link_id` BIGINT NOT NULL,
  PRIMARY KEY (`mentee_mentor_program_id`, `useful_link_id`),
  INDEX `mmpul_ul_fk_idx` (`useful_link_id` ASC),
  CONSTRAINT `mmpul_p_fk`
    FOREIGN KEY (`mentee_mentor_program_id`)
    REFERENCES mentee_mentor_program (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `mmpul_ul_fk`
    FOREIGN KEY (`useful_link_id`)
    REFERENCES `useful_link` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_mentor_program_document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_program_document` (
  `mentee_mentor_program_id` BIGINT NOT NULL,
  `document_id` BIGINT NOT NULL,
  PRIMARY KEY (`mentee_mentor_program_id`, `document_id`),
  INDEX `mmpd_d_fk0_idx` (`document_id` ASC),
  CONSTRAINT `mmpd_p_fk`
    FOREIGN KEY (`mentee_mentor_program_id`)
    REFERENCES `mentee_mentor_program` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `mmpd_d_fk0`
    FOREIGN KEY (`document_id`)
    REFERENCES `document` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mentee_mentor_goal_useful_link`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_goal_useful_link` (
  `mentee_mentor_goal_id` BIGINT NOT NULL,
  `useful_link_id` BIGINT NOT NULL,
  PRIMARY KEY (`mentee_mentor_goal_id`, `useful_link_id`),
  INDEX `mmpul_ul_fk_idx` (`useful_link_id` ASC),
  CONSTRAINT `mmgul_g_fk0`
    FOREIGN KEY (`mentee_mentor_goal_id`)
    REFERENCES `mentee_mentor_goal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `mmpul_ul_fk0`
    FOREIGN KEY (`useful_link_id`)
    REFERENCES `useful_link` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mentee_mentor_goal_document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_goal_document` (
  `mentee_mentor_goal_id` BIGINT NOT NULL,
  `document_id` BIGINT NOT NULL,
  PRIMARY KEY (`mentee_mentor_goal_id`, `document_id`),
  INDEX `mmpd_d_fk0_idx` (`document_id` ASC),
  CONSTRAINT `mmgd_g_fk0`
    FOREIGN KEY (`mentee_mentor_goal_id`)
    REFERENCES `mentee_mentor_goal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `mmgd_d_fk00`
    FOREIGN KEY (`document_id`)
    REFERENCES `document` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(1024) NOT NULL,
  `path` VARCHAR(1024) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_on` DATETIME NOT NULL,
  `last_modified_by` BIGINT NOT NULL,
  `last_modified_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `img_url_idx` (`url` ASC))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mentee_mentor_program_request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mentee_mentor_program_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `mentor_id` BIGINT NOT NULL,
    `mentee_id` BIGINT NOT NULL,
    `request_time` DATETIME NOT NULL,
    `status` VARCHAR(45) NULL,
    `approved_or_rejected_time` DATETIME NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `mmpr_me_fk0`
        FOREIGN KEY (`mentee_id`)
        REFERENCES `mentee` (`id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT `mmpr_m_fk0`
        FOREIGN KEY (`mentor_id`)
        REFERENCES `mentor` (`id`)
        ON DELETE CASCADE
        ON UPDATE NO ACTION

) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `country_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `country_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `country_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_country_locale_lc` (`locale_id`, `country_id`),
  INDEX `cl_lo_fk_idx` (`locale_id`),
  INDEX `cl_co_fk_idx` (`country_id`),
  CONSTRAINT `cl_lo_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `cl_co_fk`
      FOREIGN KEY (`country_id`)
      REFERENCES `country` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `personal_interest_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `personal_interest_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `personal_interest_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_personal_interest_locale_lc` (`locale_id`, `personal_interest_id`),
  INDEX `pil_lo_fk_idx` (`locale_id`),
  INDEX `pil_pei_fk_idx` (`personal_interest_id`),
  CONSTRAINT `pil_lo_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `pil_pei_fk`
      FOREIGN KEY (`personal_interest_id`)
      REFERENCES `personal_interest` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `professional_consultant_area_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professional_consultant_area_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `professional_consultant_area_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_professional_consultant_area_locale_lc` (`locale_id`, `professional_consultant_area_id`),
  INDEX `pcal_l_fk_idx` (`locale_id`),
  INDEX `pcal_pca_fk_idx` (`professional_consultant_area_id`),
  CONSTRAINT `pcal_l_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `pcal_pca_fk`
      FOREIGN KEY (`professional_consultant_area_id`)
      REFERENCES `professional_consultant_area` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `professional_interest_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professional_interest_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `professional_interest_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_professional_interest_locale_lc` (`locale_id`, `professional_interest_id`),
  INDEX `pil_l_fk_idx` (`locale_id`),
  INDEX `pil_pi_fk_idx` (`professional_interest_id`),
  CONSTRAINT `pil_l_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `pil_pi_fk`
      FOREIGN KEY (`professional_interest_id`)
      REFERENCES `professional_interest` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `skill_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `skill_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `skill_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_skill_locale_lc` (`locale_id`, `skill_id`),
  INDEX `skl_l_fk_idx` (`locale_id`),
  INDEX `skl_s_fk_idx` (`skill_id`),
  CONSTRAINT `skl_l_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `skl_s_fk`
      FOREIGN KEY (`skill_id`)
      REFERENCES `skill` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `state_locale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `state_locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(256) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `state_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_state_locale_lc` (`locale_id`, `state_id`),
  INDEX `stl_l_fk_idx` (`locale_id`),
  INDEX `stl_s_fk_idx` (`state_id`),
  CONSTRAINT `stl_l_fk`
      FOREIGN KEY (`locale_id`)
      REFERENCES `locale` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION,
  CONSTRAINT `stl_s_fk`
      FOREIGN KEY (`state_id`)
      REFERENCES `state` (`id`)
      ON DELETE CASCADE
      ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE FUNCTION `calculate_distance` (`longitude1` DECIMAL (16, 8),  `latitude1` DECIMAL (16, 8),`longitude2` DECIMAL (16, 8),  `latitude2` DECIMAL (16, 8)) RETURNS DECIMAL(16, 8) RETURN ST_Distance_Sphere(Point(longitude1, latitude1), Point(longitude2, latitude2));
