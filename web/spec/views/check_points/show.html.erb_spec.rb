require 'spec_helper'

describe "check_points/show" do
  before(:each) do
    @check_point = assign(:check_point, stub_model(CheckPoint))
  end

  it "renders attributes in <p>" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
  end
end
