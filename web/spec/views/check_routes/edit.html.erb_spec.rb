require 'spec_helper'

describe "check_routes/edit" do
  before(:each) do
    @check_route = assign(:check_route, stub_model(CheckRoute))
  end

  it "renders the edit check_route form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", check_route_path(@check_route), "post" do
    end
  end
end
