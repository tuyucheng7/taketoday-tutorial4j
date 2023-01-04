package cn.tuyucheng.taketoday.themes.repository;

import cn.tuyucheng.taketoday.themes.domain.UserPreference;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPreferenceRepository extends PagingAndSortingRepository<UserPreference, String> {
}