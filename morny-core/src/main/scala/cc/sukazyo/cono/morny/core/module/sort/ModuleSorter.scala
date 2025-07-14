package cc.sukazyo.cono.morny.core.module.sort

import cc.sukazyo.cono.morny.core.module.MornyModule
import scalax.collection.edges.DiEdgeImplicits
import scalax.collection.generic.Edge
import scalax.collection.mutable.Graph

import scala.collection.mutable

/** Tools that can sort modules by its dependency definition, with its id and provides.
  */
object ModuleSorter {
	
	/** Some modules have the same module ID, or provides the same module ID.
	  * @since 0.2.0-alpha21
	  */
	class DuplicatedModuleException (val moduleID: String, val duplicatedModules: List[MornyModule])
		extends Exception(s"The following module provides the same module ID: ${duplicatedModules.map(_.id).mkString(", ")}")
	/** The [[sourceModule]] requires [[missingModuleId]] module, but that module is not found
	  * in provided modules.
	  * @since 0.2.0-alpha21
	  */
	class MissingRequiredModuleException (val sourceModule: MornyModule, val missingModuleId: String)
		extends Exception(s"Module $missingModuleId is not found but is required by ${sourceModule.id}" )
	/** Some modules dependency relation have cycled. Like a depends b, b depends c, but c
	  * depends a.
	  * @since 0.2.0-alpha21
	  */
	class CycleModuleDependencyException (val dependencyCycleModules: Set[MornyModule])
		extends Exception("The following modules have cyclic dependency relations: "
			+ dependencyCycleModules.map(_.id).mkString(", "))
	
	private type DependencyEdge = Edge[MornyModule]
	private type ModuleDependencyGraph = Graph[MornyModule, DependencyEdge]
	
	@throws[DuplicatedModuleException]
	@throws[MissingRequiredModuleException]
	private def createGraph (modules: List[MornyModule]): ModuleDependencyGraph = {
		
		// init module id->module map
		val __moduleMap = mutable.HashMap.empty[String, MornyModule]
		modules.foreach(i =>
			for (id <- i.id :: i.provide.toList)
				__moduleMap.get(id).map: it =>
					throw DuplicatedModuleException(id, i :: it :: Nil)
				__moduleMap += (id -> i)
		)
		val moduleMap = __moduleMap.toMap
		
		// init graph nodes
		val graph: ModuleDependencyGraph = Graph.empty
		modules.foreach(graph+=_)
		
		// init graph edges
		// a ~> b means that b depends on a (b should load after a)
		for (module <- modules) {
			for (requiresId <- module.requires) {
				if !moduleMap.contains(requiresId) then
					throw MissingRequiredModuleException(module, requiresId)
			}
			for (dependsId <- module.depends) {
				moduleMap.get(dependsId) match
					case None => throw MissingRequiredModuleException(module, dependsId)
					case Some(dependsModule) =>
						graph.add(dependsModule ~> module)
			}
			for (afterId <- module.after) {
				moduleMap.get(afterId) match
					case None =>
					case Some(afterModule) =>
						graph.add(afterModule ~> module)
			}
			for (beforeId <- module.before) {
				moduleMap.get(beforeId) match
					case None =>
					case Some(beforeModule) =>
						graph.add(module ~> beforeModule)
			}
		}
		
		graph
		
	}
	
	/** Sorts the modules by its dependency relations.
	  *
	  * Note: Current version uses graph's topological sort. Due to some technical limitations,
	  * the sorted order is unstable.
	  *
	  * @param modules The modules that should be ordered.
	  * @throws DuplicatedModuleException      Some modules provide the same module ID.
	  * @throws MissingRequiredModuleException Some module is requiring another module, but that
	  *                                        module is not found under provided modules.
	  * @throws CycleModuleDependencyException Modules dependency relation have cycled.
	  * @return Sorted module list. The earlier module should be load before.
	  */
	@throws[DuplicatedModuleException]
	@throws[MissingRequiredModuleException]
	@throws[CycleModuleDependencyException]
	def sort (modules: List[MornyModule]): List[MornyModule] = {
		val graph = createGraph(modules)
		graph.topologicalSort match
			case Left(ring) => throw CycleModuleDependencyException(ring.candidateCycleNodes.map(_.source))
			case Right(sortedModules) => sortedModules.toList.map(_.source)
	}
	
}
