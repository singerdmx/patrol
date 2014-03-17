require "spec_helper"

describe CheckRoutesController do
  describe "routing" do

    it "routes to #index" do
      get("/check_routes").should route_to("check_routes#index")
    end

    it "routes to #new" do
      get("/check_routes/new").should route_to("check_routes#new")
    end

    it "routes to #show" do
      get("/check_routes/1").should route_to("check_routes#show", :id => "1")
    end

    it "routes to #edit" do
      get("/check_routes/1/edit").should route_to("check_routes#edit", :id => "1")
    end

    it "routes to #create" do
      post("/check_routes").should route_to("check_routes#create")
    end

    it "routes to #update" do
      put("/check_routes/1").should route_to("check_routes#update", :id => "1")
    end

    it "routes to #destroy" do
      delete("/check_routes/1").should route_to("check_routes#destroy", :id => "1")
    end

  end
end
