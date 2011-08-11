jQuery(function($){
    window.Commit = Backbone.Model.extend({

    });

    window.CommitList = Backbone.Collection.extend({
        url: '/commits',
        model: Commit
    });

    window.CommitView = Backbone.View.extend({
        tagName: "tr",
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
            
            this.nextAlt = true;
        },

        addOne: function(commit) {
			var view = new CommitView({model: commit});
			var el = view.render().el
			this.$("#commit-list").append(el);
			if (this.nextAlt) {
				$(el).addClass("alt");
			}
			this.nextAlt = !this.nextAlt;
        },

        addAll: function() {
            Commits.each(this.addOne);
        },
        
        scroll: function() {
        	console.log( $(window).scrollTop() + " vs " + $(document).height() + " - " + $(window).height() + " == " + ($(document).height() - $(window).height()) )
			if ($(window).scrollTop() + 50 >= $(document).height() - $(window).height()){
				Commits.fetch({ add: true, data: jQuery.param({from: Commits.size()}) })
			}
        }
    });

    window.Commits = new CommitList;

    window.App = new AppView;
});