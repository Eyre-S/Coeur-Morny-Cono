package cc.sukazyo.cono.morny.util.dataview

object Table {
	
	def format (title: Seq[Any], data: Seq[Any]*): String = {
		val table = data.prepended(title)
		if (table.isEmpty) ""
		else {
			// Get column widths based on the maximum cell width in each column (+2 for a one character padding on each side)
			val colWidths = table.transpose.map(_.map(cell => if (cell == null) 0 else cell.toString.length).max + 2)
			// Format each row
			val rows = table.map(_.zip(colWidths).map { case (item, size) => (" %-" + (size - 1) + "s").format(item) }
				.mkString("|", "|", "|"))
			// Formatted separator row, used to separate the header and draw table borders
			val separator = colWidths.map("-" * _).mkString("+", "+", "+")
			// Put the table together and return
			(separator +: rows.head +: separator +: rows.tail :+ separator).mkString("\n")
		}
	}
	
}
