package hu.juzraai.harvey.data

import com.j256.ormlite.dao.Dao
import hu.juzraai.toolbox.data.OrmLiteDatabase

/**
 * @author Zsolt Jurányi
 */
class HarveyDao(val db: OrmLiteDatabase) {

	val batchDao = db.dao(Batch::class.java) as Dao<Batch, String>
	val stateDao = db.dao(State::class.java) as Dao<State, Long>
	val taskDao = db.dao(Task::class.java) as Dao<Task, String>

	fun storeBatch(batch: Batch) {
		batchDao.createIfNotExists(batch)
	}

	fun storeTask(task: Task): Int {
		return if (taskDao.createIfNotExists(task).importedAt == task.importedAt) 1 else 0
	}
}