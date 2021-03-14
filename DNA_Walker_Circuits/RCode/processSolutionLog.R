
setwd("E:/DNA")
#install.packages("ggplot2")
library(ggplot2)

directories = unlist(strsplit(list.dirs(path = ".", recursive = F), " "))

directories = "./TestAlgoV3_1000_Iteration_0"

dirs <- list()

for(i in 1:length(directories)){
  
  #print(grep("^./Iteration\\d+$", directories[i]))

  if(length(grep("^./NewTest_\\d+$", directories[i])) > 0){

    dirs <- c(dirs, directories[i])

  }
  
}



for(y in 1:length(dirs)){
  
  fileName = paste0(dirs[y],"/BestSolutionsLog.txt")
  
  #unlist(strsplit(unlist(dirs[y]),"_"))[2]
  
  
  iteration = substr(dirs[y],11, nchar(dirs[y]))
  
  data = read.table(fileName, header = T, sep = "\t", stringsAsFactors = F)
  
  data$BestFitness <- as.numeric(data$BestFitness)
  data$ID <- seq(1:nrow(data))
  
  #png(filename = paste0("E:/DNA/",iteration,".png"), width = 1000, height = 600)
  
  ggplot(data, aes(x = ID, y = BestFitness)) +
    geom_point() + geom_line() +
    scale_x_continuous(breaks=seq(0,max(data$ID,1))) +
    labs(title=paste0("Best Fitness - NewTest ",iteration), 
         x ="Solution", y = "Fitness")
   #dev.off()
   
   ggsave(filename = paste0("E:/DNA/NewTest_",iteration,"_bestFitness.png"), width = 9, height = 7)
  
   # x = c(1:nrow(data))
   # data <- cbind(x,data)
   # 
   # ggplot(data, aes(x = x, y = Fitness, group = 1)) +
   #   geom_point() +
   #   geom_line() +
   #   #scale_x_discrete(breaks=seq(0,nrow(data),1)) +
   #   labs(title=paste0("Fitness for each iteration - ",iteration,"-",y, " (algoV3)"),
   #        x ="Iteration", y = "Frequency")
   # 
   # 
   # ggsave(filename = paste0("E:/DNA/",iteration,"_",y,"_fitness__algoV4.png"), width = 9, height = 7)
   
}


errorFiles = data[data$Fitness==0,]
nrow(errorFiles)
filesToCopy = errorFiles$FileName
targetdir = c("E:/DNA/OuputFiles")

file.copy(from=filesToCopy, to=targetdir)

gsub(".candl",".txt", filesToCopy[1])


filesToCopy = lapply(filesToCopy, function(x) gsub("ValidSolutions","OutputFiles", x))

filesToCopy = lapply(filesToCopy, function(x) gsub(".candl",".txt", x))


