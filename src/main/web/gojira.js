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
            Commits.bind('reset', this.addAll, this);

            Commits.fetch({ data: jQuery.param({from: Commits.size()}) })
        },

        addOne: function(commit) {
			var view = new CommitView({model: commit});
			this.$("#commit-list").append(view.render().el);
        },

        addAll: function() {
            Commits.each(this.addOne);
        },
    });

    window.Commits = new CommitList;

    window.App = new AppView;
});