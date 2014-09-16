package me.geso.tinyorm.annotations;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Optional;

import lombok.Data;
import me.geso.tinyorm.TestBase;

import org.junit.Test;

public class UpdatedEpochTimestampTest extends TestBase {

	@Test
	public void test() throws SQLException {
		orm.getConnection()
				.prepareStatement(
						"DROP TABLE IF EXISTS x")
				.executeUpdate();
		orm.getConnection()
				.prepareStatement(
						"CREATE TABLE x (id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255) NOT NULL, updatedOn INT UNSIGNED)")
				.executeUpdate();
		X created = orm.insert(X.class)
				.value("name", "John")
				.executeSelect();
		// filled updatedOn column
		assertTrue((created.getUpdatedOn() - System.currentTimeMillis() / 1000) < 3);
		// clear updatedOn column
		orm.getConnection()
				.prepareStatement(
						"UPDATE x SET updatedOn=NULL")
				.executeUpdate();
		// updated updatedOn column
		XForm form = new XForm();
		form.setName("Taro");
		orm.updateByBean(created, form);
		Optional<X> maybeUpdated = orm.refetch(created);
		assertTrue(maybeUpdated.isPresent());
		X updated = maybeUpdated.get();
		assertNotNull(updated);
		assertNotNull(updated.getUpdatedOn());
		assertTrue((updated.getUpdatedOn() - System.currentTimeMillis() / 1000) < 3);
	}

	@Data
	@Table("x")
	public static class X {
		@PrimaryKey
		private long id;

		@Column
		private String name;

		@UpdatedTimestampColumn
		private Long updatedOn;
	}
	
	@Data
	public static class XForm {
		private String name;
	}

}
