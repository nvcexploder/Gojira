jQuery(function($){
    window.Commit = Backbone.Model.extend({
        attributes : ["id", "hash", "issues"]
    });

    window.CommitList = Backbone.Collection.extend({
        url: '/commits',
        model: Commit
    });

    window.CommitView = Backbone.View.extend({
        tagName: "li",
        template: $("#item-template").template(),
        render: function() {
            var element = jQuery.tmpl(this.template, this.model.toJSON());
            $(this.el).html(element);
            return this;
        }
    });

    window.AppView = Backbone.View.extend({
        el: $("#app"),
        initialize: function() {
        	$(window).bind("scroll", this.scroll);
        	
            Commits.bind("reset", this.addAll, this);
            Commits.bind("add", this.addOne, this);
            
            Commits.fetch();
        },

        addOne: function(commit) {
			var view = new CommitView({model: commit});
			this.$("#commit-list").append(view.render().el);
        },

        addAll: function() {
            Commits.each(this.addOne);
        },
        
        scroll: function() {
			if ($(window).scrollTop() == $(document).height() - $(window).height()){
				Commits.fetch({ add: true, data: jQuery.param({from: Commits.size()}) })
			}
        }
    });

    window.Commits = new CommitList;

    window.App = new AppView;
});