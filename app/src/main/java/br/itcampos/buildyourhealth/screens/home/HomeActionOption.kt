package br.itcampos.buildyourhealth.screens.home

enum class HomeActionOption(val title: String) {
    EditTraining("Edit Training"),
    DeleteTraining("Delete Training");

    companion object {
        fun getByTitle(title: String): HomeActionOption {
            values().forEach { action -> if (title == action.title) return action }
            return EditTraining
        }

        fun getOptions(hasEditOption: Boolean): List<HomeActionOption> {
            val options = mutableListOf<HomeActionOption>()
            values().forEach { taskAction ->
                if (hasEditOption || taskAction != EditTraining) {
                    options.add(taskAction)
                }
            }
            return options
        }
    }
}