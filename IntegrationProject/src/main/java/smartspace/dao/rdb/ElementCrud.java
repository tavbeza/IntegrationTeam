package smartspace.dao.rdb;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import smartspace.data.ElementEntity;

public interface ElementCrud extends PagingAndSortingRepository<ElementEntity, String> {

	public List<ElementEntity> findAllByCreationTimestampBetween(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate, Pageable pageable);

	public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByTypeLikeAndExpiredIsFalse(@Param("type") String type, Pageable pageable);

	public List<ElementEntity> findAllByLocation_XBetweenAndLocation_YBetweenAndExpiredIsFalse(
			@Param("minX") double minX, @Param("maxX") double maxX, @Param("minY") double minY,
			@Param("maxY") double maxY, Pageable pageable);

	public List<ElementEntity> findAllByNameLikeAndExpiredIsFalse(@Param("name") String name, Pageable pageable);

}
