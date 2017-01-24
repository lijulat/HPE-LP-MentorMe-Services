package com.livingprogress.mentorme.security;

import com.livingprogress.mentorme.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import static org.springframework.http.HttpMethod.*;

/**
 * The application security config.
 */
@Configuration
@EnableWebSecurity
@Order(1)
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * The user detail service.
     */
    @Autowired
    private UserDetailsService userDetailsService;



    /**
     * The stateless auth filter.
     */
    @Autowired
    private StatelessAuthenticationFilter statelessAuthenticationFilter;

    /**
     * The password encoder.
     *
     * @return password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Helper.getPasswordEncoder();
    }

    /**
     * Configure global auth manager builder.
     *
     * @param auth the auth manager builder
     * @throws Exception throws if any error happen
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(Helper.getPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(authProvider);
        auth.userDetailsService(userDetailsService);
    }

    /**
     * Create auth manager bean.
     *
     * @return the auth manager bean.
     * @throws Exception throws if any error happen
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configure authentication.
     *
     * @param http the http
     * @throws Exception if there is any error
     */
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        http.csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(statelessAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(entryPoint)
            .and()
            .anonymous()
            .and()
            .servletApi()
            .and()
            .headers()
            .cacheControl()
            .and()
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll()
                //allow anonymous for lookup,forgot password, update password requests
                .antMatchers("/favicon.ico")
                .permitAll()
                .antMatchers("/lookups/**")
                .permitAll()
                .antMatchers("/users/forgotPassword")
                .permitAll()
                .antMatchers("/users/updatePassword")
                .permitAll()
                .antMatchers(GET, "/institutions")
                .permitAll()
                .antMatchers(GET, "/images/{imageUrl}")
                .permitAll()
                .antMatchers(POST, "/users")
                .hasAuthority("SYSTEM_ADMIN")
                .antMatchers(DELETE, "/users/{id}")
                .hasAuthority("SYSTEM_ADMIN")
                .antMatchers(POST, "/institutions")
                .hasAuthority("SYSTEM_ADMIN")
                .antMatchers(PUT, "/institutions/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/institutions/{id}")
                .hasAuthority("SYSTEM_ADMIN")
                .antMatchers(PUT, "/institutions/{id}/generateAffiliationCode")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(PUT, "/mentees/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,MENTEE")
                .antMatchers(DELETE, "/mentees/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,MENTEE")
                .antMatchers(POST, "/mentors/register")
                .permitAll()
                .antMatchers(POST, "/mentees/register")
                .permitAll()
                .antMatchers(PUT, "/mentors/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,MENTOR")
                .antMatchers(DELETE, "/mentors/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,MENTOR")
                .antMatchers(GET, "/mentors/linkedInExperience").hasAuthority("MENTOR")
                .antMatchers(POST, "/menteeMentorPrograms")
                .hasAnyAuthority("MENTOR,MENTEE,INSTITUTION_ADMIN")
                .antMatchers(PUT, "/menteeMentorPrograms/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/menteeMentorPrograms/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(PUT, "/menteeMentorPrograms/{id}/menteeFeedback")
                .hasAuthority("MENTEE")
                .antMatchers(PUT, "/menteeMentorProgramRequests/{id}")
                .hasAnyAuthority("MENTOR,MENTEE")
                .antMatchers(DELETE, "/menteeMentorProgramRequests/{id}")
                .hasAnyAuthority("MENTOR,MENTEE")
                .antMatchers(GET, "/menteeMentorProgramRequests")
                .hasAnyAuthority("MENTOR,MENTEE")
                .antMatchers(GET, "/menteeMentorProgramRequests/{id}")
                .hasAnyAuthority("MENTOR,MENTEE")
                .antMatchers(POST, "/menteeMentorProgramRequests")
                .hasAnyAuthority("MENTEE,MENTOR")
                .antMatchers(PUT, "/menteeMentorPrograms/{id}/mentorFeedback")
                .hasAuthority("MENTEE")
                .antMatchers(PUT, "/menteeMentorPrograms/{id}/menteeFeedback")
                .hasAuthority("MENTOR")
                .antMatchers(POST, "/documents/{entityType}/{entityId}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/documents/{entityType}/{entityId}/document/{documentId}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/usefulLinks/{entityType}/{entityId}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/usefulLinks/{entityType}/{entityId}/link/{linkId}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/menteeMentorTasks")
                .hasAuthority("MENTOR")
                .antMatchers(PUT, "/menteeMentorTasks/{id}")
                .hasAnyAuthority("MENTOR,MENTEE")
                .antMatchers(DELETE, "/menteeMentorTasks/{id}")
                .hasAnyAuthority("MENTOR")
                .antMatchers(POST, "/menteeMentorGoals")
                .hasAnyAuthority("MENTOR")
                .antMatchers(PUT, "/menteeMentorGoals/{id}")
                .hasAnyAuthority("MENTOR")
                .antMatchers(DELETE, "/menteeMentorGoals/{id}")
                .hasAnyAuthority("MENTOR")
                .antMatchers(POST, "/menteeMentorResponsibilities")
                .hasAuthority("MENTOR")
                .antMatchers(PUT, "/menteeMentorResponsibilities/{id}")
                .hasAuthority("MENTOR")
                .antMatchers(DELETE, "/menteeMentorResponsibilities/{id}")
                .hasAuthority("MENTOR")
                .antMatchers(POST, "/institutionAgreements")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(PUT, "/institutionAgreements/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/institutionAgreements/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(POST, "/goals")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/goals/{id}")
                .hasAnyAuthority("INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/goals/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/tasks") // mentee can only update task but cannot create
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/tasks/{id}")
                .hasAnyAuthority("MENTOR,MENTEE,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/tasks/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/institutionalPrograms")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(POST, "/institutionalPrograms/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(POST, "/institutionalPrograms/{id}/clone")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/institutionalPrograms/{id}")
                .hasAnyAuthority("SYSTEM_ADMIN,INSTITUTION_ADMIN")
                .antMatchers(POST, "/responsibilities")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(PUT, "/responsibilities/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(DELETE, "/responsibilities/{id}")
                .hasAnyAuthority("MENTOR,INSTITUTION_ADMIN")
                .antMatchers(POST, "/institutionalPrograms/{id}/clone")
                .hasAnyAuthority("MENTOR")
                 //allow anonymous calls to social login
                .antMatchers("/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(entryPoint);
    }
}

