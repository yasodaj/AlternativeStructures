library(igraph)
library(tidyverse)
library(ggraph)
library(plyr)
library(tidyr)


getEdgeListToy <- function(){
  
  E <- matrix(c(0, 1, 0, 0, 0, 0,
                0, 0, 1, 1, 0, 0,
                0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 1,
                0, 0, 1, 0, 0, 0,
                0, 0, 0, 1, 0, 0), nrow = 6, byrow = T) %>%
    data.frame() %>% 
    rename_all(list(function(x) 1:6)) %>% 
    rownames_to_column(var = "from") %>% 
    gather(to, val, 2:5) %>% 
    filter(val == 1) %>%
    select(from, to)
  
  return(E)
  
}

drawLayoutsToy <- function(fileName,imageSaveDirectory,fit,ordr){
 
    
    layoutString = imageSaveDirectory
    #layoutString = gsub(",","", layoutString)
    corrdinatePairs = laply(seq(1, nchar(layoutString), 2), function(i) substr(layoutString, i, i+1))
    
    temp <- data.frame(Node = c(1:length(corrdinatePairs)), cPairs = corrdinatePairs)
    vertexList <- separate(data = temp, col = cPairs, into = c("x", "y"), sep = 1)
    vertexList$Node <- as.character(vertexList$Node)
    
    for(i in 2:3){
      vertexList[,i] <- revalue(vertexList[,i], c("a"="10"))
      vertexList[,i] <- revalue(vertexList[,i], c("b"="11"))
      vertexList[,i] <- revalue(vertexList[,i], c("c"="12"))
      vertexList[,i] <- revalue(vertexList[,i], c("d"="13"))
      vertexList[,i]<-as.numeric(vertexList[,i])
    }
    
    
    # vertexList$x <- as.integer(vertexList$x)
    # vertexList$y <- as.integer(vertexList$y)
    
    #edge list
    edgeListToy <- getEdgeListToy()
    
    g <- graph_from_data_frame(edgeListToy, vertices = vertexList, directed = F)
    
    graphName = paste0(fileName,ordr,"_",imageSaveDirectory,"_",fit,".png")
    nodeLables <- c(" I ", " F ", "N1", "N2", "F1", "F2")
    nodeColours <- c("#3333FF", "#00CC00","#FFFFFF", "#FFFFFF", "#FF3366","#FF3366")
    
    
    png(graphName, width = 500, height = 500)
    
    gPlot<- ggraph(g) + 
      geom_edge_link(edge_width = 1.3) + 
      geom_node_point(size = 10, color = nodeColours)+
      geom_node_point(size = 10,pch=21,color="black")+
      geom_node_text(aes(label = nodeLables))+
      ggtitle(paste0("Toy Layout "),layoutString) +
      coord_flip() +
      expand_limits(x = 0, y = 0) +
      
      # Using scale_x_reverse and swapping the limits
      scale_x_reverse(expand = c(0, 0), limits = c(14, 0), breaks = c(0:14), minor_breaks = NULL) + 
      # switching y position to "right" (pre-flip)
      scale_y_continuous(expand = c(0, 0),limits = c(0, 14), breaks = c(0:14), minor_breaks = NULL, position = "right") +
      theme_minimal()
    
    print(gPlot)
    
    dev.off()
    
}


folderN = "E:\\SA\\Toy0\\NewGridSize_30000I\\"

for(i in 1:1){

datEliet <- read.table(paste0(folderN,"Run",i,"\\BestSolutionsLog.txt"), 
                       header = T, stringsAsFactors = F,
                       row.names = NULL)

colnames(datEliet) <- c("Iteration","Fit","Elitef", "Elitex", 
                        "InitialSolution", "NewSolution") 

uniquElitexInitSolutions = as.data.frame(table(datEliet$Elitex))

uniquElitexInitSolutions$IterationIDs <- 0

for(d in 1:nrow(uniquElitexInitSolutions)){
  
  s = as.character(uniquElitexInitSolutions[d,1])
  #rowNumbers = which(datNew$Solution == s, arr.ind = TRUE)
  rowNumbers = datEliet[datEliet$Elitex==s,]$Iteration
  uniquElitexInitSolutions[d,3] <- paste(as.character(rowNumbers), collapse=", ")
  
}

uniquElitexInitSolutions$Fitness <- 0

for(v in 1:nrow(uniquElitexInitSolutions)){
  
  s = as.character(uniquElitexInitSolutions[v,1])
  fitness = datEliet[datEliet$Elitex == s,3][1]
  uniquElitexInitSolutions[v,4] <- fitness
  
}


uniquElitexInitSolutions <- uniquElitexInitSolutions[order(uniquElitexInitSolutions$Fitness, decreasing = TRUE),]

write.table(as.character(unlist(uniquElitexInitSolutions$Var1)),
            paste0(folderN,"Run9\\Run9_uniqueElite.txt"), row.names = F, quote = F, col.names = F,
            sep = "\n")


#with the two escape sequence at the end of the name
#e.g. E:\\TestDavidSolutions\\Toy1\\Test4\\Layouts\\
uniquElitexInitSolutions[,1]
  
  for(s in 1:nrow(uniquElitexInitSolutions)){
    
    fileName = paste0(folderN,"Run",i,"\\")
    imageNamePrefix = as.character(uniquElitexInitSolutions[s,1])
    fitness = as.character(uniquElitexInitSolutions[s,4])
    
    drawLayoutsToy(fileName, imageNamePrefix,fitness,s)
    
  }

}

