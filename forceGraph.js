    var width = 500, height = 500;
    var flag = false;
    var step = 0.5;
    var force = d3.layout.force()
        .charge(-40)
        .linkDistance(1)
        .linkStrength(1)
        .size([width, height]);
    var svg = d3.select("body").append("svg")
        .attr("width", width)
        .attr("height", height);

    d3.json("http://localhost:8080/main", function(error, graph) {
        if (error) throw error;
        force
            .nodes(graph.nodes)
            .links(graph.links)
            .start();

        var link = svg.selectAll(".link")
            .data(graph.links)
            .enter().append("line")
            .attr("class", "link")
            .style("stroke", function(d) { return "rgb("+d.color.red.toString()+","+d.color.green.toString()+","+ d.color.blue.toString()+")"; })
            .style("stroke-opacity", function(d) { return d.opacity; })
            .style("stroke-width", function(d) { return Math.sqrt(d.width); })
            ;

        var color = d3.scale.category20();
        var defs = svg.append("defs").attr("id", "imgdefs");
        var node = svg.selectAll(".node")
            .data(graph.nodes)
            .enter().append("circle")
            .attr("class", "node")
            .attr("name", function(d){return d.name;})
            .attr("r", function(d) { return d.radius })
            .style("fill", color[3])
            .attr("fixed", true)
            .call(force.drag);

        force.on("tick", function() {
            link.attr("x1", function(d) { return d.source.x; })
                .attr("y1", function(d) { return d.source.y; })
                .attr("x2", function(d) { return d.target.x; })
                .attr("y2", function(d) { return d.target.y; });

            node.attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; });
        });
    });
    print("hi");






