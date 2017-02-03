package com.livingprogress.mentorme.remote.utils;

import com.livingprogress.mentorme.entities.InstitutionUser;
import com.livingprogress.mentorme.entities.PersonalInterest;
import com.livingprogress.mentorme.entities.ProfessionalInterest;
import com.livingprogress.mentorme.entities.User;
import com.livingprogress.mentorme.entities.WeightedPersonalInterest;
import com.livingprogress.mentorme.entities.WeightedProfessionalInterest;
import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.InterestCategory;
import com.livingprogress.mentorme.utils.Helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class provides help methods used in this application.
 */
public class RemoteHelper {

    /**
     * The private constructor.
     */
    private RemoteHelper() {
    }


    /**
     * Get categories include parent parent category from mentors/mentees.
     *
     * @param entity the entity.
     * @param <T> the entity class.
     * @return the interest category.
     */
    public static <T extends InstitutionUser> InterestCategory getCategories(T entity) {
        InterestCategory result = new InterestCategory();
        result.setInterestCategories(new ArrayList<>());
        result.setParentInterestCategories(new ArrayList<>());
        result.setCategories(new ArrayList<>());
        entity.getProfessionalInterests()
              .stream()
              .sorted(Comparator.comparing(WeightedProfessionalInterest::getWeight)
                                .reversed())
              .forEach(c -> {
                  ProfessionalInterest parent = c.getProfessionalInterest()
                                                 .getParentCategory();
                  if (parent != null) {
                      result.getParentInterestCategories()
                            .add(parent.getValue());
                  }
                  result.getInterestCategories()
                        .add(c.getProfessionalInterest()
                              .getValue());
              });
        entity.getPersonalInterests()
              .stream()
              .sorted(Comparator.comparing(WeightedPersonalInterest::getWeight)
                                .reversed())
              .forEach(c -> {
                  PersonalInterest parent = c.getPersonalInterest()
                                             .getParentCategory();
                  if (parent != null) {
                      result.getParentInterestCategories().add(parent.getValue());
                  }
                  result.getInterestCategories().add(c.getPersonalInterest()
                                                               .getValue());

              });
        result.getCategories().addAll(result.getInterestCategories());
        result.getCategories().addAll(result.getParentInterestCategories());
        return result;
    }

    /**
     * Get address information from user.
     *
     * @param user the user.
     * @return the address information.
     * @throws IllegalArgumentException if user is null or invalid.
     */
    public static String getAddress(User user) {
        Helper.checkNull(user, "user");
        if (user.getCountry() == null) {
            return null;
        }
        List<String> addressInfos = new ArrayList<>();
        if (user.getStreetAddress() != null) {
            addressInfos.add(user.getStreetAddress());
        }
        if (user.getCity() != null) {
            addressInfos.add(user.getCity());
        }
        if (user.getState() != null) {
            addressInfos.add(user.getState()
                    .getValue() + (user.getPostalCode() == null ? "" : " " + user.getPostalCode()));
        } else if (user.getPostalCode() != null) {
            addressInfos.add(user.getPostalCode());
        }
        addressInfos.add(user.getCountry()
                             .getValue());
        return String.join(Constant.COMMA, addressInfos);
    }
}
