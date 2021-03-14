library(igraph)
library(tidyverse)
library(ggraph)
library(plyr)
library(tidyr)


getEdgeListToy2 <- function(){
  
  #I,N0,F,N11,N12,N21,N22,F1,F2
  
  E <- matrix(c(0, 1, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 1, 0, 0, 0,
                0, 0, 1, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 0, 0, 1, 0,
                0, 0, 1, 0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 1,
                0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0), nrow = 9, byrow = T) %>%
    data.frame() %>% 
    rename_all(list(function(x) 1:9)) %>% 
    rownames_to_column(var = "from") %>% 
    gather(to, val, 2:8) %>% 
    filter(val == 1) %>%
    select(from, to)
  
  return(E)
  
}


drawLayoutsToy2 <- function(fileName, imageSaveDirectory){
  
  layouts = read.table(fileName, stringsAsFactors = F, header = F)
  
  for(n in 1:nrow(layouts)){
    
    layoutString = as.character(layouts[[n,1]])
    
    corrdinatePairs = laply(seq(1, nchar(layoutString), 2), function(i) substr(layoutString, i, i+1))
    
    temp <- data.frame(Node = c(1:length(corrdinatePairs)), cPairs = corrdinatePairs)
    vertexList <- separate(data = temp, col = cPairs, into = c("x", "y"), sep = 1)
    vertexList$Node <- as.character(vertexList$Node)
    
    for(i in 2:3){
      vertexList[,i] <- revalue(vertexList[,i], c("a"="10"))
      vertexList[,i] <- revalue(vertexList[,i], c("b"="11"))
      vertexList[,i] <- revalue(vertexList[,i], c("c"="12"))
      vertexList[,i] <- revalue(vertexList[,i], c("d"="13"))
      vertexList[,i] <- revalue(vertexList[,i], c("e"="14"))
      vertexList[,i] <- revalue(vertexList[,i], c("f"="15"))
      vertexList[,i] <- revalue(vertexList[,i], c("g"="16"))
      vertexList[,i] <- revalue(vertexList[,i], c("h"="17"))
      vertexList[,i] <- revalue(vertexList[,i], c("i"="18"))
      vertexList[,i] <- revalue(vertexList[,i], c("j"="19"))
      vertexList[,i]<-as.numeric(vertexList[,i])
    }
    
    #edge list
    edgeListToy2 <- getEdgeListToy2()
    
    g <- graph_from_data_frame(edgeListToy2, vertices = vertexList, directed = F)
    
    graphName = paste0(imageSaveDirectory,layoutString,".png")
    nodeLables <- c(" I ", "N0" , "F", " N11 ", "N12", "N21", "N22", "F1", "F2")
    nodeColours <- c("#3333FF", "#FFFFFF", "#00CC00","#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FF3366","#FF3366")
    
    png(graphName, width = 500, height = 500)
    
    gPlot<- ggraph(g) + 
      geom_edge_link(edge_width = 1.3) + 
      geom_node_point(size = 10, color = nodeColours)+
      geom_node_point(size = 10,pch=21,color="black")+
      geom_node_text(aes(label = nodeLables))+
      ggtitle(paste0("Toy 2 Layout "),layoutString) +
      coord_flip() +
      expand_limits(x = 0, y = 0) +
      
      # Using scale_x_reverse and swapping the limits
      scale_x_reverse(expand = c(0, 0), limits = c(20, 0), breaks = c(0:20), minor_breaks = NULL) + 
      # switching y position to "right" (pre-flip)
      scale_y_continuous(expand = c(0, 0),limits = c(0, 20), breaks = c(0:20), minor_breaks = NULL, position = "right") +
      theme_minimal()
    print(gPlot)
    
    dev.off()
    
    
    
  }
}


#with the two escape sequence at the end of the name
#e.g. E:\\TestDavidSolutions\\Toy1\\Test4\\Layouts\\
imageSaveDirectory = "E:\\TestDavidSolutions\\NewGridSize\\Toy2\\"

fileName = "E:\\TestDavidSolutions\\NewGridSize\\Toy2\\testToy2.txt"

drawLayoutsToy2(fileName, imageSaveDirectory)





dev.off()

