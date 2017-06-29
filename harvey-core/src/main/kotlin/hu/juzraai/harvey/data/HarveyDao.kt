package hu.juzraai.harvey.data

import com.j256.ormlite.dao.*
import hu.juzraai.toolbox.data.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
@Suppress("UNCHECKED_CAST")
open class HarveyDao(val db: OrmLiteDatabase) {

	companion object : KLogging()

	val batchDao = db.dao(Batch::class.java) as Dao<Batch, String>
	val stateDao = db.dao(State::class.java) as Dao<State, String>
	val taskDao = db.dao(Task::class.java) as Dao<Task, String>

	open fun storeBatch(batch: Batch) {
		logger.trace("Storing batch: {}", batch)
		batchDao.createIfNotExists(batch)
	}

	open fun storeState(state: State) {
		logger.trace("Storing state: {}", state)
		stateDao.createOrUpdate(state)
	}

	open fun storeTask(task: Task): Int {
		logger.trace("Storing task: {}", task)
		return if (taskDao.createIfNotExists(task).importedAt == task.importedAt) 1 else 0
	}

	open fun tasksOfBatch(batchId: String): List<Task> {
		logger.debug("Fetching tasks of batch: {}", batchId)
		val batch = batchDao.queryBuilder().where().eq("batchId", batchId).query()
		return batch.map {
			taskDao.refresh(it.task!!)
			it.task!!
		}
	}
}